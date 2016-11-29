from eve import Eve
from eve.auth import BasicAuth
import bcrypt
import uuid

class CheckAuth(BasicAuth):
    _account = None
    
    def check_auth(self, username, password, allowed_roles, resource, method):
        accounts = app.data.driver.db['users']
        account = accounts.find_one({'login': username})
        CheckAuth._account = account
        result = account and bcrypt.hashpw(password.encode('utf-8'), account['salt']) == account['password']
        if result == False :
            return result
        if username == 'guest':
            return allow_guest(resource, method)
        if username != 'admin':
            return allow_users(resource, method)
        return True
    
        def get_account (self) :
            return self._account
    
                
def allow_guest(resource, method) :
    if method == 'POST' and resource == 'users' :
        return True
    if method != 'GET':
        return False
    if method == 'GET' :
        if resource == 'sections' or resource == 'chapters' : 
            return True
        if resource == 'users' :
            return True
    return False
                
def allow_users(resource, method) :
    if method == 'POST' and resource == 'user_answers' :
        return True
    if method == 'DELETE' and resource == 'user_answers' :
        return True
    if method == 'GET' :
        return True    
    return False

def insert_user (documents) :
    for document in documents:
        document['issystem'] = False
        document['salt'] = bcrypt.gensalt()
        document['password'] = bcrypt.hashpw(document['password'].encode('utf-8'), document['salt'])
        
def delete_user (item) :
    if item['issystem'] == True:
        abort(404)

def fetched_users (response) :
    curr_user_name = CheckAuth._account['login']
    for_remove_list = []
    for document in response['_items']:
        del(document['password'])
        if curr_user_name == 'admin' :
            continue
        if curr_user_name != document['login'] :
            for_remove_list.append(document)
    for for_remove in for_remove_list:
        response['_items'].remove(for_remove)

def update_changes (documents) :
    app.data.driver.db['changes'].remove({})
    for document in documents:
        document['guid'] = uuid.uuid1().urn[9:]

def insert_chapters (documents) :
    chapters = app.data.driver.db['chapters']
    for document in documents:
        exists_chapters = chapters.find({'sectionid': document['sectionid']})
        for chapter in exists_chapters:
            if chapter['roworder'] == document['roworder']:
                abort(404)

def fetched_user_answers(response) :
    curr_user_id = CheckAuth._account['_id']
    curr_user_name = CheckAuth._account['login']
    for_remove_list = []
    for document in response['_items']:
        if curr_user_name == 'admin' :
            continue
        if curr_user_id != document['userid'] :
            for_remove_list.append(document)
    for for_remove in for_remove_list:
        response['_items'].remove(for_remove)

def delete_user_answers(document):
    curr_user_id = CheckAuth._account['_id']
    curr_user_name = CheckAuth._account['login']
    if curr_user_name != 'admin':
        if document['userid'] != curr_user_id:
            abort(401)

def log (text) :
    f = open('log.txt', 'a')
    f.write(text + '\n')
    f.close()
    

app = Eve(auth = CheckAuth)

app.on_insert_users += insert_user
app.on_delete_item_users += delete_user
app.on_fetched_resource_users += fetched_users
app.on_insert_changes += update_changes

app.on_insert_chapters += insert_chapters

app.on_fetched_resource_user_answers += fetched_user_answers
app.on_delete_item_user_answers += delete_user_answers

if __name__ == '__main__':
    app.run()

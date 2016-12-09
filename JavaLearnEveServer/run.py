from eve import Eve
from eve.auth import BasicAuth
import bcrypt

class CheckAuth(BasicAuth):
	def check_auth(self, username, password, allowed_roles,
			resource, method):
            accounts = app.data.driver.db['users']
            account = accounts.find_one({'login': username})
            return account and bcrypt.hashpw(password.encode('utf-8'), account['salt']) \
                == account['password']

#def fetch_sections_remove_description(response):
#    for document in response['_items']:
#        del(document['description'])

def create_user (documents) :
    for document in documents:
        document['salt'] = bcrypt.gensalt()
        document['password'] = bcrypt.hashpw(document['password'].encode('utf-8'), document['salt'])

#def update_user (documents) :
#    for document in documents:
#        document['salt'] = bcrypt.gensalt().encode('utf-8')
#        password = document['password'].encode('utf-8')
#        document['password'] = bcrypt.hashpw(password, document['salt'])

app = Eve(auth=CheckAuth)
app.on_insert_users += create_user

#app.on_update_users += update_user
#app.on_fetched_resource_sections += fetch_sections_remove_description

if __name__ == '__main__':
	app.run()

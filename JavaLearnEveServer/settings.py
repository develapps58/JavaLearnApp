MONGO_HOST = 'localhost'
MONGO_PORT = 27017

MONGO_DBNAME = 'JavaLearn'

RESOURCE_METHODS = ['GET', 'POST', 'DELETE']
ITEM_METHODS = ['GET', 'PATCH', 'PUT', 'DELETE']

#PUBLIC_METHODS = ['GET', 'PATCH', 'POST', 'DELETE']

X_DOMAINS = '*'
X_HEADERS = ['Content-Type', 'Accept', 'If-Match', 'Authorization', 'Cache-Control', 'Pragma', 'Expires']

DOMAIN = {
    'users': {
        #current request: users/<ObjectId>
        #add new request users/<login>
        'additional_lookup': {
            'url': "regex('[\w]+')",
            'field': 'login',
        },

        #disable cache
        'cache_control': '',
        'cache_expires': 0,

        'schema': {
            'full_name': {
                'type': 'string',
                'required': True
            },
            'login': {
                'type': 'string',
                'minlength': 5,
                'maxlength': 50,
                'required': True,
                'unique': True
            },
            'password': {
                'type': 'string',
                'required': True
            },
            'token': {
                'type': 'string',
                'unique': True
            },
            'roles': {
                'type': 'list',
                'allowed': ['admin', 'user', 'guest'],
                'required': True
            }
        }
    },
    'sections': {
    
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'title': { 'type': 'string' },
            'description': { 'type': 'string' },
            'order': { 'type': 'int' }
        }
    },
    'chapters': {
    
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'title': { 'type': 'string' },
            'content': { 'type': 'string' },
            'order': { 'type': 'int' },
            'sectionid': {
                'type': 'objectid',
                'data_relation': {
                    'resource': 'sections',
                    'field': '_id',
                    'embeddable': True
                }
            }
        }
    }
}


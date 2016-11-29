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
            'issystem': {
                'type': 'boolean',
                'required': True
            }
        }
    },
    'sections': {
    
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'title': { 'type': 'string', 'required': True },
            'description': { 'type': 'string' },
            'roworder': { 'type': 'integer', 'required': True, 'unique': True }
        }
    },
    'chapters': {
    
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'title': { 'type': 'string', 'required': True },
            'content': { 'type': 'string', 'required': True },
            'roworder': { 'type': 'integer', 'required': True },
            'sectionid': {
                'type': 'objectid',
                'required': True,
                'data_relation': {
                    'resource': 'sections',
                    'field': '_id',
                    'embeddable': True
                }
            }
        }
    },
    'questions': {
    
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'title': {'type': 'string', 'required': True },
            'roworder': { 'type': 'integer', 'required': True },
            'sectionid': {
                'type': 'objectid',
                'required': True,
                'data_relation': {
                    'resource': 'sections',
                    'field': '_id',
                    'embeddable': True
                }
            }
        }
    },
    'answers': {
    
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'title': {'type': 'string', 'required': True},
            'iscorrect': {'type': 'boolean', 'required': True},
            'roworder': { 'type': 'integer', 'required': True },
            'questionid': {
                'type': 'objectid',
                'required': True,
                'data_relation': {
                    'resource': 'questions',
                    'field': '_id',
                    'embeddable': True
                }
            }
        }
    },
    'user_answers': {
        
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'userid': {
                'type': 'objectid',
                'required': True,
                'data_relation': {
                    'resource': 'users',
                    'field': '_id',
                    'embeddable': True
                }
            },
            'answerid': {
                'type': 'objectid',
                'required': True,
                'data_relation': {
                    'resource': 'answers',
                    'field': '_id',
                    'embeddable': True
                }
            },
        }
    },
    
    'changes': {
    
        #disable cache
        'cache_control': '',
        'cache_expires': 0,
        
        'schema': {
            'guid': {
                'type': 'string', 
                'required': True,
                'unique': True
            }
        }
    },
}


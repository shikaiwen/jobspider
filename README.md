
数据存储用mongodb
DB:test
collections: csdn_expert


db.getCollection('csdn_expert').update({},{'$set':{'crawlCnt':0}},false,true)

db.getCollection('csdn_expert').find()
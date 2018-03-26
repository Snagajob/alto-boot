INSERT INTO corpora (corpus_name) VALUES ('near_global_20160101_20180101_sample_30');

INSERT INTO labels (corpus_id, label_name, label_source, user_id) VALUES
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'admin and paraprofessional', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'personal instruction/tutoring and event services', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'pet and animal services', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'automotive and vehicle maintenence services', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'beauty and grooming services', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'childcare and early education', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'corporate professional and internships', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'in-home housekeeping maintenance repair services', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'exclude spanish language', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'food and beverage', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'health and wellness', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'health and wellness professional', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'hospitality', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'industrial warehouse and manufacturing', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'industrial warehouse and manufacturing professional', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'military recruiting and contracting', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'real estate sales and related services', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'contracting and construction', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'retail', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'retail grocery', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'security services', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'trucking and transportation specialized licensed', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='near_global_20160101_20180101_sample_30'), 'trucking and transportation taxi and delivery', 'DEFAULT', (select user_id from users where user_name='system'))
    ; 

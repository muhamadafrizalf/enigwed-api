-- public.tbl_customer definition

-- Drop table

-- DROP TABLE public.tbl_customer;

CREATE TABLE public.tbl_customer (
                                     id varchar(255) NOT NULL,
                                     address varchar(1000) NULL,
                                     email varchar(255) NULL,
                                     "name" varchar(255) NULL,
                                     phone varchar(255) NULL,
                                     CONSTRAINT tbl_customer_pkey PRIMARY KEY (id)
);


-- public.tbl_image definition

-- Drop table

-- DROP TABLE public.tbl_image;

CREATE TABLE public.tbl_image (
                                  id varchar(255) NOT NULL,
                                  conten_type varchar(255) NULL,
                                  "name" varchar(255) NULL,
                                  "path" varchar(255) NULL,
                                  "size" int8 NULL,
                                  CONSTRAINT tbl_image_pkey PRIMARY KEY (id)
);


-- public.tbl_notification definition

-- Drop table

-- DROP TABLE public.tbl_notification;

CREATE TABLE public.tbl_notification (
                                         id varchar(255) NOT NULL,
                                         channel varchar(255) NULL,
                                         created_at timestamp(6) NULL,
                                         data_id varchar(255) NULL,
                                         data_type varchar(255) NULL,
                                         message varchar(255) NULL,
                                         "read" bool NOT NULL,
                                         read_at timestamp(6) NULL,
                                         receiver varchar(255) NULL,
                                         receiver_id varchar(255) NULL,
                                         "type" varchar(255) NULL,
                                         CONSTRAINT tbl_notification_channel_check CHECK (((channel)::text = ANY ((ARRAY['EMAIL'::character varying, 'SYSTEM'::character varying])::text[]))),
                                         CONSTRAINT tbl_notification_data_type_check CHECK (((data_type)::text = ANY ((ARRAY['WEDDING_ORGANIZER'::character varying, 'ORDER'::character varying])::text[]))),
                                         CONSTRAINT tbl_notification_pkey PRIMARY KEY (id),
                                         CONSTRAINT tbl_notification_receiver_check CHECK (((receiver)::text = ANY ((ARRAY['ADMIN'::character varying, 'WEDDING_ORGANIZER'::character varying, 'CUSTOMER'::character varying])::text[]))),
                                         CONSTRAINT tbl_notification_type_check CHECK (((type)::text = ANY ((ARRAY['ACCOUNT_REGISTRATION'::character varying, 'ORDER_RECEIVED'::character varying, 'ORDER_ACCEPTED'::character varying, 'ORDER_REJECTED'::character varying, 'ORDER_CANCELLED'::character varying, 'CONFIRM_PAYMENT'::character varying, 'ORDER_PAID'::character varying, 'ORDER_FINISHED'::character varying, 'ORDER_REVIEWED'::character varying])::text[])))
);

-- public.tbl_user_credential definition

-- Drop table

-- DROP TABLE public.tbl_user_credential;

CREATE TABLE public.tbl_user_credential (
                                            id varchar(255) NOT NULL,
                                            created_at timestamp(6) NULL,
                                            deleted_at timestamp(6) NULL,
                                            updated_at timestamp(6) NULL,
                                            active bool NOT NULL,
                                            email varchar(255) NULL,
                                            "password" varchar(255) NULL,
                                            "role" varchar(255) NULL,
                                            CONSTRAINT tbl_user_credential_pkey PRIMARY KEY (id),
                                            CONSTRAINT tbl_user_credential_role_check CHECK (((role)::text = ANY ((ARRAY['ROLE_ADMIN'::character varying, 'ROLE_WO'::character varying])::text[])))
);

-- public.tbl_province definition

-- Drop table

-- DROP TABLE public.tbl_province;

CREATE TABLE public.tbl_province (
                                     id varchar(255) NOT NULL,
                                     "name" varchar(255) NULL,
                                     CONSTRAINT tbl_province_pkey PRIMARY KEY (id)
);


-- public.tbl_regency definition

-- Drop table

-- DROP TABLE public.tbl_regency;

CREATE TABLE public.tbl_regency (
                                    id varchar(255) NOT NULL,
                                    "name" varchar(255) NULL,
                                    province_id varchar(255) NULL,
                                    CONSTRAINT tbl_regency_pkey PRIMARY KEY (id),
                                    CONSTRAINT fknqxtalos1h1cqtdawkfv1vvyl FOREIGN KEY (province_id) REFERENCES public.tbl_province(id)
);


-- public.tbl_district definition

-- Drop table

-- DROP TABLE public.tbl_district;

CREATE TABLE public.tbl_district (
                                     id varchar(255) NOT NULL,
                                     "name" varchar(255) NULL,
                                     regency_id varchar(255) NULL,
                                     CONSTRAINT tbl_district_pkey PRIMARY KEY (id),
                                     CONSTRAINT fkopsishi9x16mdsry8y1lmqt20 FOREIGN KEY (regency_id) REFERENCES public.tbl_regency(id)
);


-- public.tbl_wedding_organizer definition

-- Drop table

-- DROP TABLE public.tbl_wedding_organizer;

CREATE TABLE public.tbl_wedding_organizer (
                                              id varchar(255) NOT NULL,
                                              created_at timestamp(6) NULL,
                                              deleted_at timestamp(6) NULL,
                                              updated_at timestamp(6) NULL,
                                              address varchar(1000) NULL,
                                              description varchar(1000) NULL,
                                              "name" varchar(255) NULL,
                                              nib varchar(255) NULL,
                                              npwp varchar(255) NULL,
                                              phone varchar(255) NULL,
                                              avatar_id varchar(255) NULL,
                                              district_id varchar(255) NULL,
                                              province_id varchar(255) NULL,
                                              regency_id varchar(255) NULL,
                                              user_credential_id varchar(255) NULL,
                                              CONSTRAINT tbl_wedding_organizer_pkey PRIMARY KEY (id),
                                              CONSTRAINT uk41w30xdvky0ljf6nn7oqwkvx7 UNIQUE (avatar_id),
                                              CONSTRAINT uk78x0xkd1p2yvu96av5kugtnpw UNIQUE (user_credential_id),
                                              CONSTRAINT fk3kjg8uyrm432icw15pv4avj5o FOREIGN KEY (province_id) REFERENCES public.tbl_province(id),
                                              CONSTRAINT fkn9243nqogbskfixf8msm0l8ca FOREIGN KEY (regency_id) REFERENCES public.tbl_regency(id),
                                              CONSTRAINT fkoix21r06nxr41rw0uf1go7dw9 FOREIGN KEY (district_id) REFERENCES public.tbl_district(id),
                                              CONSTRAINT fkpbwcm7min5owgi7bncbct2jpn FOREIGN KEY (avatar_id) REFERENCES public.tbl_image(id),
                                              CONSTRAINT fktivtbr3mh0bwiob7tmw6verj8 FOREIGN KEY (user_credential_id) REFERENCES public.tbl_user_credential(id)
);

-- public.tbl_product definition

-- Drop table

-- DROP TABLE public.tbl_product;

CREATE TABLE public.tbl_product (
                                    id varchar(255) NOT NULL,
                                    created_at timestamp(6) NULL,
                                    deleted_at timestamp(6) NULL,
                                    updated_at timestamp(6) NULL,
                                    description varchar(1000) NULL,
                                    "name" varchar(255) NULL,
                                    price float8 NOT NULL,
                                    wedding_organizer_id varchar(255) NULL,
                                    CONSTRAINT tbl_product_pkey PRIMARY KEY (id),
                                    CONSTRAINT fk5ao142mx7370iiw4xn47vvlwd FOREIGN KEY (wedding_organizer_id) REFERENCES public.tbl_wedding_organizer(id)
);


-- public.bonus_package_image definition

-- Drop table

-- DROP TABLE public.bonus_package_image;

CREATE TABLE public.bonus_package_image (
                                            bonus_package_id varchar(255) NOT NULL,
                                            image_id varchar(255) NOT NULL,
                                            CONSTRAINT fk5ex9irrynkt3i0knbl1tsakf5 FOREIGN KEY (image_id) REFERENCES public.tbl_image(id),
                                            CONSTRAINT fkfwtl1cx6gtx4rrspjhlccb2tq FOREIGN KEY (bonus_package_id) REFERENCES public.tbl_product(id)
);

-- public.tbl_wedding_package definition

-- Drop table

-- DROP TABLE public.tbl_wedding_package;

CREATE TABLE public.tbl_wedding_package (
                                            id varchar(255) NOT NULL,
                                            created_at timestamp(6) NULL,
                                            deleted_at timestamp(6) NULL,
                                            updated_at timestamp(6) NULL,
                                            description varchar(10000) NULL,
                                            "name" varchar(255) NULL,
                                            order_count int4 NULL,
                                            price float8 NOT NULL,
                                            province_id varchar(255) NULL,
                                            regency_id varchar(255) NULL,
                                            wedding_organizer_id varchar(255) NULL,
                                            CONSTRAINT tbl_wedding_package_pkey PRIMARY KEY (id),
                                            CONSTRAINT fke2w238aqyd7estwasumarfvsj FOREIGN KEY (regency_id) REFERENCES public.tbl_regency(id),
                                            CONSTRAINT fkhh08x91brfg6asyngn553rbki FOREIGN KEY (wedding_organizer_id) REFERENCES public.tbl_wedding_organizer(id),
                                            CONSTRAINT fkqbcc4g7vesdwjlhed53ren0sb FOREIGN KEY (province_id) REFERENCES public.tbl_province(id)
);


-- public.wedding_package_image definition

-- Drop table

-- DROP TABLE public.wedding_package_image;

CREATE TABLE public.wedding_package_image (
                                              wedding_package_id varchar(255) NOT NULL,
                                              image_id varchar(255) NOT NULL,
                                              CONSTRAINT fk1wxd6b5nmnxnsversnppwmgo7 FOREIGN KEY (wedding_package_id) REFERENCES public.tbl_wedding_package(id),
                                              CONSTRAINT fkcxjneylw91s8n21v2cc49auxv FOREIGN KEY (image_id) REFERENCES public.tbl_image(id)
);


-- public.tbl_bonus_detail definition

-- Drop table

-- DROP TABLE public.tbl_bonus_detail;

CREATE TABLE public.tbl_bonus_detail (
                                         id varchar(255) NOT NULL,
                                         quantity int4 NOT NULL,
                                         product_id varchar(255) NULL,
                                         wedding_package_id varchar(255) NULL,
                                         CONSTRAINT tbl_bonus_detail_pkey PRIMARY KEY (id),
                                         CONSTRAINT fk7jwgu2g24yp93hw9d1d0f7eab FOREIGN KEY (wedding_package_id) REFERENCES public.tbl_wedding_package(id),
                                         CONSTRAINT fkh3yv06rolcq00knabpx5eooxf FOREIGN KEY (product_id) REFERENCES public.tbl_product(id)
);


-- public.tbl_order definition

-- Drop table

-- DROP TABLE public.tbl_order;

CREATE TABLE public.tbl_order (
                                  id varchar(255) NOT NULL,
                                  base_price float8 NULL,
                                  book_code varchar(255) NOT NULL,
                                  reviewed bool NOT NULL,
                                  status varchar(255) NULL,
                                  total_price float8 NULL,
                                  transaction_date timestamp(6) NULL,
                                  transaction_finish_date timestamp(6) NULL,
                                  updated_at timestamp(6) NULL,
                                  wedding_date timestamp(6) NULL,
                                  customer_id varchar(255) NULL,
                                  payment_image_id varchar(255) NULL,
                                  wedding_organizer_id varchar(255) NULL,
                                  wedding_package_id varchar(255) NULL,
                                  review_id varchar(255) NULL,
                                  CONSTRAINT tbl_order_pkey PRIMARY KEY (id),
                                  CONSTRAINT tbl_order_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'REJECTED'::character varying, 'WAITING_FOR_PAYMENT'::character varying, 'CHECKING_PAYMENT'::character varying, 'PAID'::character varying, 'CANCELED'::character varying, 'FINISHED'::character varying])::text[]))),
                                  CONSTRAINT uk5epbfdyur2amj3h1n4lpdt7ku UNIQUE (review_id),
                                  CONSTRAINT ukhakvasw5sc50gscd7ik3g2twy UNIQUE (customer_id),
                                  CONSTRAINT ukm94n5m07bej9w1hneh0dbwpkl UNIQUE (book_code)
);


-- public.tbl_order_detail definition

-- Drop table

-- DROP TABLE public.tbl_order_detail;

CREATE TABLE public.tbl_order_detail (
                                         id varchar(255) NOT NULL,
                                         bonus bool NOT NULL,
                                         price float8 NOT NULL,
                                         quantity int4 NOT NULL,
                                         order_id varchar(255) NULL,
                                         product_id varchar(255) NULL,
                                         CONSTRAINT tbl_order_detail_pkey PRIMARY KEY (id)
);


-- public.tbl_review definition

-- Drop table

-- DROP TABLE public.tbl_review;

CREATE TABLE public.tbl_review (
                                   id varchar(255) NOT NULL,
                                   "comment" varchar(255) NULL,
                                   customer_name varchar(255) NULL,
                                   rating float8 NOT NULL,
                                   order_id varchar(255) NULL,
                                   wedding_organizer_id varchar(255) NULL,
                                   wedding_package_id varchar(255) NULL,
                                   CONSTRAINT tbl_review_pkey PRIMARY KEY (id),
                                   CONSTRAINT uk80j7kl2ji0t9pbunvvbi7r7dr UNIQUE (order_id)
);


-- public.tbl_order foreign keys

ALTER TABLE public.tbl_order ADD CONSTRAINT fk3gb6a3uhqnd6soveh4iu6iubb FOREIGN KEY (wedding_organizer_id) REFERENCES public.tbl_wedding_organizer(id);
ALTER TABLE public.tbl_order ADD CONSTRAINT fk4wtagjt0duej1vjnvgaita4f1 FOREIGN KEY (review_id) REFERENCES public.tbl_review(id);
ALTER TABLE public.tbl_order ADD CONSTRAINT fkdbbvj4affcu7x13tjn0a12htw FOREIGN KEY (customer_id) REFERENCES public.tbl_customer(id);
ALTER TABLE public.tbl_order ADD CONSTRAINT fkjcvjm2yitvc1ei544e3fhelwg FOREIGN KEY (wedding_package_id) REFERENCES public.tbl_wedding_package(id);
ALTER TABLE public.tbl_order ADD CONSTRAINT fkq3mholvtnk04wlvr4c2yv1ehc FOREIGN KEY (payment_image_id) REFERENCES public.tbl_image(id);


-- public.tbl_order_detail foreign keys

ALTER TABLE public.tbl_order_detail ADD CONSTRAINT fkh1jxpk342vagdcrf5o8lpxd1c FOREIGN KEY (product_id) REFERENCES public.tbl_product(id);
ALTER TABLE public.tbl_order_detail ADD CONSTRAINT fknjncq9emp0cdhj0xbe3kk06h8 FOREIGN KEY (order_id) REFERENCES public.tbl_order(id);


-- public.tbl_review foreign keys

ALTER TABLE public.tbl_review ADD CONSTRAINT fk6ha689lawp00uhopqhqikj8qc FOREIGN KEY (wedding_organizer_id) REFERENCES public.tbl_wedding_organizer(id);
ALTER TABLE public.tbl_review ADD CONSTRAINT fk6kaseapsqd7am5egfxa9r13rw FOREIGN KEY (order_id) REFERENCES public.tbl_order(id);
ALTER TABLE public.tbl_review ADD CONSTRAINT fkjaxrcidkrbnyw0klbn53btvaw FOREIGN KEY (wedding_package_id) REFERENCES public.tbl_wedding_package(id);

INSERT INTO public.tbl_province (id,"name") VALUES
                                                ('32','JAWA BARAT'),
                                                ('35','JAWA TIMUR'),
                                                ('61','KALIMANTAN BARAT'),
                                                ('74','SULAWESI TENGGARA'),
                                                ('75','GORONTALO'),
                                                ('11','ACEH'),
                                                ('72','SULAWESI TENGAH'),
                                                ('63','KALIMANTAN SELATAN'),
                                                ('15','JAMBI'),
                                                ('76','SULAWESI BARAT');
INSERT INTO public.tbl_province (id,"name") VALUES
                                                ('94','PAPUA'),
                                                ('13','SUMATERA BARAT'),
                                                ('14','RIAU'),
                                                ('16','SUMATERA SELATAN');

INSERT INTO public.tbl_regency (id,"name",province_id) VALUES
                                                           ('3207','KABUPATEN CIAMIS','32'),
                                                           ('3273','KOTA BANDUNG','32'),
                                                           ('3578','KOTA SURABAYA','35'),
                                                           ('3271','KOTA BOGOR','32'),
                                                           ('6104','KABUPATEN MEMPAWAH','61'),
                                                           ('7405','KABUPATEN KONAWE SELATAN','74'),
                                                           ('7502','KABUPATEN GORONTALO','75'),
                                                           ('3516','KABUPATEN MOJOKERTO','35'),
                                                           ('1103','KABUPATEN ACEH SELATAN','11'),
                                                           ('7202','KABUPATEN BANGGAI','72');
INSERT INTO public.tbl_regency (id,"name",province_id) VALUES
                                                           ('6305','KABUPATEN TAPIN','63'),
                                                           ('1506','KABUPATEN TANJUNG JABUNG TIMUR','15'),
                                                           ('7403','KABUPATEN KONAWE','74'),
                                                           ('7602','KABUPATEN POLEWALI MANDAR','76'),
                                                           ('9403','KABUPATEN JAYAPURA','94'),
                                                           ('1302','KABUPATEN PESISIR SELATAN','13'),
                                                           ('1114','KABUPATEN ACEH TAMIANG','11'),
                                                           ('1403','KABUPATEN INDRAGIRI HILIR','14'),
                                                           ('1605','KABUPATEN MUSI RAWAS','16'),
                                                           ('3201','KABUPATEN BOGOR','32');

INSERT INTO public.tbl_district (id,"name",regency_id) VALUES
                                                           ('3207100','BANJARSARI','3207'),
                                                           ('3273110','CIBIRU','3273'),
                                                           ('3578190','GENTENG','3578'),
                                                           ('3271040','BOGOR TENGAH','3271'),
                                                           ('6104101','MEMPAWAH TIMUR','6104'),
                                                           ('7405081','WOLASI','7405'),
                                                           ('7502070','LIMBOTO','7502'),
                                                           ('3516140','GEDEK','3516'),
                                                           ('1103010','TRUMON','1103'),
                                                           ('7202012','MOILONG','7202');
INSERT INTO public.tbl_district (id,"name",regency_id) VALUES
                                                           ('6305040','BUNGUR','6305'),
                                                           ('1506032','MUARA SABAK TIMUR','1506'),
                                                           ('7403091','LALONGGASUMEETO','7403'),
                                                           ('7602012','LIMBORO','7602'),
                                                           ('9403082','YAPSI','9403');

INSERT INTO public.tbl_image (id,conten_type,"name","path","size") VALUES
                                                                       ('51bdcb66-530c-47ef-98d2-d49c36fa0582',NULL,NULL,NULL,NULL),
                                                                       ('47c17b53-aae7-4eca-b243-42053b8a1af6',NULL,NULL,NULL,NULL),
                                                                       ('7e73af09-a2c4-443d-b519-a9fd24e3b47c',NULL,NULL,NULL,NULL),
                                                                       ('e8c51c1e-3c24-4090-84f9-4403debb9917',NULL,NULL,NULL,NULL),
                                                                       ('f6da8b56-70b3-4d93-82af-0c28d54b749d',NULL,NULL,NULL,NULL),
                                                                       ('3d874979-f570-42ef-805a-6b413ec9b442',NULL,NULL,NULL,NULL),
                                                                       ('661fd2b9-1ec0-45e8-b00b-e76c6c1767ef',NULL,NULL,NULL,NULL),
                                                                       ('b4f6e128-8604-46f0-8fed-e0c88a97d99e',NULL,NULL,NULL,NULL),
                                                                       ('0e11a050-c594-46a4-be32-0100714c62ca',NULL,NULL,NULL,NULL),
                                                                       ('9764d683-3e5c-4e62-abe1-b2dc8f97b055',NULL,NULL,NULL,NULL);
INSERT INTO public.tbl_image (id,conten_type,"name","path","size") VALUES
                                                                       ('cef5df2a-c7bf-4472-bf26-f8173af693d0',NULL,NULL,NULL,NULL),
                                                                       ('9659f1cb-efa3-4ac7-b837-da74e3318e36',NULL,NULL,NULL,NULL),
                                                                       ('b88d95af-15a4-4682-82a6-b6613717e13a',NULL,NULL,NULL,NULL),
                                                                       ('21c48eb7-2b82-4e91-af30-d4638a5fc5c2',NULL,NULL,NULL,NULL),
                                                                       ('47d1e071-967d-4163-986e-a0a4cfe2dbc2',NULL,NULL,NULL,NULL);

INSERT INTO public.tbl_user_credential (id,created_at,deleted_at,updated_at,active,email,"password","role") VALUES
                                                                                                                ('c35aced7-7d08-4225-9d07-55f9abc10c17','2024-11-06 23:09:39.015075',NULL,'2024-11-06 23:09:39.015098',true,'admin@enigwed.com','$2a$10$RD6WsXqZIKiAnZ3oJQjKZuiuTCdPoyCCzTiTumYHRo4nanDykm/Ey','ROLE_ADMIN'),
                                                                                                                ('fd650833-18b7-4c52-94b5-35aea05467ae','2024-11-06 23:42:32.159203',NULL,'2024-11-07 00:09:54.055506',true,'sahsahsah@gmail.com','$2a$10$t56cLHjy4SG9pUvtzWj0qOJ26U1I5cTQ52K/yuQdDg4B0Fl60DNAu','ROLE_WO'),
                                                                                                                ('20833ae1-dd26-4002-b6f3-09baf35b1266','2024-11-06 23:51:03.557317',NULL,'2024-11-07 00:10:20.083104',true,'cintaabadiwedding@gmail.com','$2a$10$EgXJIZkw23phRhBWDSQYTOS0r0WumZEv9MHOSPGuRf5GhfH.YDkK6','ROLE_WO'),
                                                                                                                ('0f1acc49-5783-46bd-a142-4ffa980e13cb','2024-11-06 23:53:19.893417',NULL,'2024-11-07 00:10:22.861273',true,'surabayaweddingdreams@gmail.com','$2a$10$I1LIY6uNI/izahzcDdF7ZO6WHjDI3W9shnkX.S8lho2gEekccbPV.','ROLE_WO'),
                                                                                                                ('3afaabd8-3e99-4c88-9692-7ee86871d00e','2024-11-06 23:56:22.251683',NULL,'2024-11-07 00:10:26.121672',true,'bogorelegantweddings@gmail.com','$2a$10$1fVZStDFbioCz2IEe3Btk.HJRd4Dmo5.5uJBbceiQlzTt8JYEBgg6','ROLE_WO'),
                                                                                                                ('fd67192d-e9fe-40c6-9284-223c306c242b','2024-11-07 00:25:30.649049',NULL,'2024-11-07 00:25:30.649055',false,'aaa@gmail.com','$2a$10$xkS9AT21yPuJu4fmzmL0F.ghlReeBH6H/I9iDUpPuf82DwvMxr8Fy','ROLE_WO'),
                                                                                                                ('f36f45fb-1048-49a7-96d7-372944138442','2024-11-07 00:56:24.649088',NULL,'2024-11-07 01:01:45.481815',true,'nam@gmail.com','$2a$10$GUW0HJPbmUFO/s0iEX951uctz9o2Qlzp./EymApXnUGxTQAaskPSC','ROLE_WO'),
                                                                                                                ('32682b1e-91c4-4844-9f80-412d66bbae92','2024-11-07 00:26:37.92117',NULL,'2024-11-07 01:02:38.926886',true,'name@gmail.com','$2a$10$lZCMl6AbH2LHIsOEuhJ9VO7D41lIB/fdWRYqiDNZm9Fkkh08UUpjO','ROLE_WO'),
                                                                                                                ('168edab4-5d04-4c96-b0ba-4f69293698ac','2024-11-07 00:24:58.278228',NULL,'2024-11-07 01:10:30.999039',true,'Aperiam@gmail.com','$2a$10$ffOO8luKghABd4ZY0Z6O/..QZvNZwFfpKvn8vOWuA53OhLzxAxje6','ROLE_WO'),
                                                                                                                ('ec41918e-e3e7-40b5-97f0-09626aabe277','2024-11-07 01:15:33.909098',NULL,'2024-11-07 01:16:03.800349',true,'quiz@example.com','$2a$10$NWSveZlFVrBNByK5GzAPaeyie4oz1/4H/BEfMwq2aYn/Cnd414Yty','ROLE_WO');
INSERT INTO public.tbl_user_credential (id,created_at,deleted_at,updated_at,active,email,"password","role") VALUES
                                                                                                                ('0ceddbab-97f0-4f48-a54c-7e027c023cc6','2024-11-07 02:01:23.732613',NULL,'2024-11-07 02:01:23.732619',false,'tempor@dlsajf.co','$2a$10$Mzn7ORwFn5YwPi3sxj5cl.RYtIQL1.dVh9sRQGdBXWfi.VNHstZxC','ROLE_WO'),
                                                                                                                ('761ab82a-c032-46b0-b7d5-5d72fa6eb723','2024-11-07 02:04:22.208746',NULL,'2024-11-07 02:04:22.20875',false,'fslk@dfadlf.c','$2a$10$g51PeBFbSW8E7rYUD870kOcyGLJODyaFLqG5Xd01WSmiZgB7qYyv2','ROLE_WO'),
                                                                                                                ('55e3604b-69e1-4bf9-a55a-ad4d94937df5','2024-11-07 02:13:25.979798',NULL,'2024-11-07 02:13:25.979802',false,'maxid@aldai.d','$2a$10$ALMD4Gv6ZVWSGVTGsAfTl.gIif12BvzskSP6h7UeQS9fVAvoRGSei','ROLE_WO'),
                                                                                                                ('1d2332aa-8710-41ad-a671-52d4aec813ca','2024-11-07 02:34:05.826035',NULL,'2024-11-07 02:34:05.82604',false,'har@dalkfc.dsa','$2a$10$Ms9vUee/GPl66kiGkuqTDecbcGPn2E3hNthWM7p9vlWyDKxUjzYx2','ROLE_WO'),
                                                                                                                ('f4e618c4-7a40-4106-8921-22028c2f7820','2024-11-07 02:59:34.44537',NULL,'2024-11-07 02:59:34.445375',false,'Quid@dsaflk.com','$2a$10$sb9MKaD7gPrHG0LyDTqO0uFWgvK6Fcxcl.xrLjvYzufUO0q9xvRa6','ROLE_WO'),
                                                                                                                ('9e68c604-0d8c-41b6-9318-002efff933af','2024-11-07 03:01:17.044824',NULL,'2024-11-07 03:03:08.865144',true,'E@jhsdf.dsf','$2a$10$NhC63sqQPFOj6IpnpEtKy.ftI1OAoLhpMf6czYVItVbrSzRI0dNpi','ROLE_WO');

INSERT INTO public.tbl_wedding_organizer (id,created_at,deleted_at,updated_at,address,description,"name",nib,npwp,phone,avatar_id,district_id,province_id,regency_id,user_credential_id) VALUES
                                                                                                                                                                                             ('wo-001','2024-11-06 23:42:32.230753',NULL,'2024-11-06 23:42:32.230785','Jl. In Aja Dulu No. 102','Wedding organizer ternama dari priangan timur, sukses mengesahkan lebih dari 1000 pasangan menuju pelaminan','Bismillah Sah','2222222222','1111111111','+6285161089321','51bdcb66-530c-47ef-98d2-d49c36fa0582','3207100','32','3207','fd650833-18b7-4c52-94b5-35aea05467ae'),
                                                                                                                                                                                             ('wo-002','2024-11-06 23:51:03.580523',NULL,'2024-11-06 23:51:03.580549','Jl. Merpati No. 15, Bandung','Wedding organizer profesional yang telah berpengalaman lebih dari 10 tahun, berkomitmen untuk mewujudkan pernikahan impian setiap pasangan di seluruh Indonesia.','Cinta Abadi Wedding Organizer','4444444444','3333333333','+6281234567890','47c17b53-aae7-4eca-b243-42053b8a1af6','3273110','32','3273','20833ae1-dd26-4002-b6f3-09baf35b1266'),
                                                                                                                                                                                             ('wo-003','2024-11-06 23:53:19.912561',NULL,'2024-11-06 23:53:19.912579','Jl. Raya Surabaya No. 28, Surabaya','Menyediakan layanan pernikahan premium di Surabaya, kami hadir untuk mewujudkan momen spesial Anda dengan sentuhan personal yang tak terlupakan.','Surabaya Wedding Dreams','6666666666','5555555555','+6281234567891','7e73af09-a2c4-443d-b519-a9fd24e3b47c','3578190','35','3578','0f1acc49-5783-46bd-a142-4ffa980e13cb'),
                                                                                                                                                                                             ('wo-004','2024-11-06 23:56:22.267064',NULL,'2024-11-06 23:56:22.26708','Jl. Raya Bogor No. 45, Bogor','Penyedia layanan pernikahan premium di Bogor, mengutamakan kenyamanan dan kesempurnaan dalam setiap momen pernikahan.','Bogor Elegant Weddings','1010101010','9999999999','+6281356789103','e8c51c1e-3c24-4090-84f9-4403debb9917','3271040','32','3271','3afaabd8-3e99-4c88-9692-7ee86871d00e'),
                                                                                                                                                                                             ('35f6b398-126e-480f-b559-abe289053882','2024-11-07 00:24:58.292566',NULL,'2024-11-07 00:24:58.292571','Vel omnis provident','Sunt fuga Dolore ve','Voluptas enim magnam','Nostrud voluptas sit','Dolorem culpa irure','+1 (644) 147-1665','f6da8b56-70b3-4d93-82af-0c28d54b749d','6104101','61','6104','168edab4-5d04-4c96-b0ba-4f69293698ac'),
                                                                                                                                                                                             ('f03e3647-3178-4150-81f6-451dd5264924','2024-11-07 00:25:30.660128',NULL,'2024-11-07 00:25:30.660132','Illum quo quo labor','Sit mollitia qui su','Sequi lorem necessit','Quia non sunt conse','Magnam elit necessi','+1 (577) 745-2464','3d874979-f570-42ef-805a-6b413ec9b442','7405081','74','7405','fd67192d-e9fe-40c6-9284-223c306c242b'),
                                                                                                                                                                                             ('58e22fd6-554b-4c68-ab37-6d9c0054ae3a','2024-11-07 00:26:37.931294',NULL,'2024-11-07 00:26:37.931298','Repudiandae nulla la','Dolorem minus atque ','name','Ea aut quo magna et ','Esse et maiores corp','+1 (482) 157-7879','661fd2b9-1ec0-45e8-b00b-e76c6c1767ef','7502070','75','7502','32682b1e-91c4-4844-9f80-412d66bbae92'),
                                                                                                                                                                                             ('6e49b972-4e23-4f62-9d91-15392c8d6524','2024-11-07 00:56:24.674894',NULL,'2024-11-07 00:56:24.674909','Commodi est tempore','In qui eveniet ut e','Eu voluptas iste ut ','Nostrum voluptatem ','Corrupti repellendu','+1 (131) 685-6534','b4f6e128-8604-46f0-8fed-e0c88a97d99e','3516140','35','3516','f36f45fb-1048-49a7-96d7-372944138442'),
                                                                                                                                                                                             ('831f0c88-5940-4f95-91a6-a54110fc5faf','2024-11-07 01:15:33.928239',NULL,'2024-11-07 01:15:33.928252','Laudantium tempora ','Enim voluptatem inve','Quis ex saepe sed ex','Harum voluptatem non','Et sed voluptatem ne','+1 (356) 967-6974','0e11a050-c594-46a4-be32-0100714c62ca','1103010','11','1103','ec41918e-e3e7-40b5-97f0-09626aabe277'),
                                                                                                                                                                                             ('ac66c2e9-48b9-437b-a08a-e8205fab0b30','2024-11-07 02:01:23.756662',NULL,'2024-11-07 02:01:23.756667','Repellendus Officia','Nostrud debitis sint','Error tempor labore ','Ut recusandae Excep','Nesciunt tempora qu','+1 (163) 543-6969','9764d683-3e5c-4e62-abe1-b2dc8f97b055','7202012','72','7202','0ceddbab-97f0-4f48-a54c-7e027c023cc6');
INSERT INTO public.tbl_wedding_organizer (id,created_at,deleted_at,updated_at,address,description,"name",nib,npwp,phone,avatar_id,district_id,province_id,regency_id,user_credential_id) VALUES
                                                                                                                                                                                             ('0e92fbcb-1377-4b01-81c9-17cbca2e8889','2024-11-07 02:04:22.21744',NULL,'2024-11-07 02:04:22.217444','Qui sed voluptatum d','Corporis voluptatem','Voluptate ea est in ','Reiciendis quidem re','Repudiandae consequa','+1 (698) 339-9113','cef5df2a-c7bf-4472-bf26-f8173af693d0','6305040','63','6305','761ab82a-c032-46b0-b7d5-5d72fa6eb723'),
                                                                                                                                                                                             ('1e3ea08b-b193-4ebc-a32b-3f65770a1b46','2024-11-07 02:13:26.001611',NULL,'2024-11-07 02:13:26.001616','Qui ullamco eaque qu','Soluta autem nisi il','Ipsum ex aut qui au','Elit maiores et rat','Quis asperiores eius','+1 (951) 805-2811','9659f1cb-efa3-4ac7-b837-da74e3318e36','1506032','15','1506','55e3604b-69e1-4bf9-a55a-ad4d94937df5'),
                                                                                                                                                                                             ('f216342a-3fff-4252-997e-dce63d9883b8','2024-11-07 02:34:05.84161',NULL,'2024-11-07 02:34:05.841614','Ut alias natus expli','Tempor odit non reru','Dolor in labore nisi','Quo eum tempora esse','Sed sint natus illo ','+1 (302) 289-1462','b88d95af-15a4-4682-82a6-b6613717e13a','7403091','74','7403','1d2332aa-8710-41ad-a671-52d4aec813ca'),
                                                                                                                                                                                             ('53527eb1-3b68-417d-aaf6-c4e81536f03a','2024-11-07 02:59:34.46386',NULL,'2024-11-07 02:59:34.463864','Inventore rerum aliq','Consequatur a cum u','Blanditiis architect','In aperiam voluptate','Labore aperiam cum q','+1 (113) 641-3906','21c48eb7-2b82-4e91-af30-d4638a5fc5c2','7602012','76','7602','f4e618c4-7a40-4106-8921-22028c2f7820'),
                                                                                                                                                                                             ('03c9ac18-e1ed-4019-bb98-dcd66eb0947f','2024-11-07 03:01:17.064266',NULL,'2024-11-07 03:01:17.06427','Laudantium aute eos','Sed ut exercitatione','Vitae consectetur ex','Suscipit placeat qu','Quia et nisi aut iru','+1 (548) 702-8546','47d1e071-967d-4163-986e-a0a4cfe2dbc2','9403082','94','9403','9e68c604-0d8c-41b6-9318-002efff933af');

INSERT INTO public.tbl_product (id,created_at,deleted_at,updated_at,description,"name",price,wedding_organizer_id) VALUES
                                                                                                                       ('b15935e1-b088-4579-9f21-b4c8d8231665','2024-11-07 08:17:12.867539','2024-11-07 08:18:29.184876','2024-11-07 08:18:29.186402','qwwq','qwqw',121.0,'wo-001'),
                                                                                                                       ('fae69b93-b888-4cc2-8bdc-bc645ee99500','2024-11-07 08:21:07.757908','2024-11-07 08:25:46.197681','2024-11-07 08:25:46.197975','qwqwq','qww',1212.0,'wo-001'),
                                                                                                                       ('bdfb96ca-6126-4dd7-a2da-47e8ef74c4bc','2024-11-07 08:25:56.220667','2024-11-07 08:26:00.626133','2024-11-07 08:26:00.627064','wqwqwq','qwwwqw',12121.0,'wo-001'),
                                                                                                                       ('28bfec71-6873-436f-a050-6380c7e1fe97','2024-11-07 00:22:51.451228',NULL,'2024-11-07 08:26:15.683992','A comprehensive photography package that includes full-day coverage, high-resolution photos, and a custom wedding album.','Premium Wedding Photography Package',5000000.0,'wo-001'),
                                                                                                                       ('d6d4d1ed-4af0-48b9-96eb-4cbdf3505a8a','2024-11-07 09:21:24.30143',NULL,'2024-11-07 09:21:36.034523','Fun Photo Booth ExperienceFun Photo Booth ExperienceFun Photo Booth ExperienceFun Photo Booth hhExperienceFun Photo Booth Experience','Fun Photo Booth Experience',200000.0,'wo-001'),
                                                                                                                       ('019f0681-183a-488c-9565-f46e2125132b','2024-11-07 09:21:49.024164',NULL,'2024-11-07 09:21:49.024169','Personalized FavorsPersonalized FavorsPersonalized FavorsPersonalized FavorsPersonalized Favors','Personalized Favors',300000.0,'wo-001'),
                                                                                                                       ('97ff591f-b686-420a-b457-8514ed01d06d','2024-11-07 10:17:12.208973','2024-11-07 10:19:50.215155','2024-11-07 10:19:50.216806','ada gitar, dan bass, ada drum,etc','Band',200000000000,'wo-001'),
                                                                                                                       ('e20ea31f-8b7f-4646-b36b-c9761b7bff6b','2024-11-07 10:25:22.446438',NULL,'2024-11-07 10:25:22.446479','Custom Wedding Invitations1Custom Wedding Invitations1Custom Wedding Invitations1','Custom Wedding Invitations1',120000.0,'wo-001');

INSERT INTO public.tbl_wedding_package (id,created_at,deleted_at,updated_at,description,"name",order_count,price,province_id,regency_id,wedding_organizer_id) VALUES
                                                                                                                                                                  ('ae0f49ca-968b-4d80-9d23-caec353604fd','2024-11-07 01:05:44.492904',NULL,'2024-11-07 01:05:44.492921','ini adalah paket 1','paket 1',0,15000000,'32','3207','wo-001'),
                                                                                                                                                                  ('2cd9cbe9-c714-4f98-a1db-4f3ffdf1c057','2024-11-07 08:55:09.729451',NULL,'2024-11-07 08:55:09.729457','1212','qw',0,12.0,'13','1302','wo-001'),
                                                                                                                                                                  ('fdb95327-98f7-4719-a3d4-3238a1ea7d14','2024-11-07 09:22:50.383058',NULL,'2024-11-07 09:22:50.383063','Custom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding InvitationsCustom Wedding Invitations','Custom Wedding Invitations',0,12000000,'11','1114','wo-001'),
                                                                                                                                                                  ('63d69dc3-9fcf-4450-bb66-6719b285c36d','2024-11-07 09:41:16.988331',NULL,'2024-11-07 09:41:16.988365','Wedding VideographyWedding VideographyWedding VideographyWedding VideographyWedding VideographyWedding Videography','Wedding Videography',0,20000000,'14','1403','wo-001'),
                                                                                                                                                                  ('f6aaafc1-6a7d-4902-983d-228bc3442bc7','2024-11-07 10:07:54.981972',NULL,'2024-11-07 10:07:54.982008','qwqqwqw','Personalized Favors',0,120000.0,'16','1605','wo-001'),
                                                                                                                                                                  ('20ac80bd-f667-4041-a8e2-9b79e88f42d4','2024-11-07 10:18:42.800764',NULL,'2024-11-07 10:18:42.800808','Bonus tamu undangan prabowo subianto','Wedding merah putih',0,20000000000000,'32','3201','wo-001');


INSERT INTO public.tbl_bonus_detail (id,quantity,product_id,wedding_package_id) VALUES
                                                                                    ('d9539a3c-9004-4916-b878-3480b8e4bc3e',3,'28bfec71-6873-436f-a050-6380c7e1fe97','ae0f49ca-968b-4d80-9d23-caec353604fd'),
                                                                                    ('a4e835b7-80b5-4fed-a06a-e84b7ab7ef6c',1212,'28bfec71-6873-436f-a050-6380c7e1fe97','2cd9cbe9-c714-4f98-a1db-4f3ffdf1c057'),
                                                                                    ('8ac4573e-661f-48d2-9db2-b2b21e07e77c',1,'019f0681-183a-488c-9565-f46e2125132b','fdb95327-98f7-4719-a3d4-3238a1ea7d14'),
                                                                                    ('0b178eca-8d6a-4408-8063-8a90d0709a64',2,'28bfec71-6873-436f-a050-6380c7e1fe97','fdb95327-98f7-4719-a3d4-3238a1ea7d14'),
                                                                                    ('5591a79f-76b0-4125-abd3-46be4cafa724',12,'019f0681-183a-488c-9565-f46e2125132b','fdb95327-98f7-4719-a3d4-3238a1ea7d14'),
                                                                                    ('4cb42781-ccfd-4e44-9cca-28c7d6cdf881',12,'019f0681-183a-488c-9565-f46e2125132b','63d69dc3-9fcf-4450-bb66-6719b285c36d'),
                                                                                    ('a8bc01de-7f63-4eb2-83eb-c5ab3a9b5717',13,'d6d4d1ed-4af0-48b9-96eb-4cbdf3505a8a','63d69dc3-9fcf-4450-bb66-6719b285c36d'),
                                                                                    ('ae40f35d-5d77-4a7f-91b4-e72ecf381c90',3,'019f0681-183a-488c-9565-f46e2125132b','63d69dc3-9fcf-4450-bb66-6719b285c36d'),
                                                                                    ('b767d84b-4715-4624-b649-11c609713147',121,'28bfec71-6873-436f-a050-6380c7e1fe97','f6aaafc1-6a7d-4902-983d-228bc3442bc7'),
                                                                                    ('2acac5e0-bec6-44d3-a221-710370436bc3',2,'d6d4d1ed-4af0-48b9-96eb-4cbdf3505a8a','f6aaafc1-6a7d-4902-983d-228bc3442bc7');
INSERT INTO public.tbl_bonus_detail (id,quantity,product_id,wedding_package_id) VALUES
                                                                                    ('fc9f23c3-f3fe-4b71-8ce3-bca7dca71bbe',1,'97ff591f-b686-420a-b457-8514ed01d06d','20ac80bd-f667-4041-a8e2-9b79e88f42d4'),
                                                                                    ('97ce70cf-928b-46c5-9837-1e3301803458',3,'019f0681-183a-488c-9565-f46e2125132b','20ac80bd-f667-4041-a8e2-9b79e88f42d4');

INSERT INTO public.tbl_customer (id,address,email,"name",phone) VALUES
                                                                    ('a77382ee-f258-4bd7-b219-7dae6c285f00','Jl topaz 7','uhukuhuk328@gmail.com','Alwa','82386283038'),
                                                                    ('93f7e345-8f53-4407-9fb0-2da2aba4f493','jl. topaz 6','uhukuhuk328@gmail.com','alwa2','+6283837624569');

INSERT INTO public.tbl_order (id,base_price,book_code,reviewed,status,total_price,transaction_date,transaction_finish_date,updated_at,wedding_date,customer_id,payment_image_id,wedding_organizer_id,wedding_package_id,review_id) VALUES
                                                                                                                                                                                                                                       ('1e90e34b-77b1-409b-a95d-37cc041f7fc4',15000000,'ENW-fUVuoX2M',false,'PENDING',15000000,'2024-11-07 02:07:20.330468',NULL,'2024-11-07 02:07:20.33048','2024-11-27 19:06:00','a77382ee-f258-4bd7-b219-7dae6c285f00',NULL,'wo-001','ae0f49ca-968b-4d80-9d23-caec353604fd',NULL),
                                                                                                                                                                                                                                       ('b23abf25-5b02-4692-9af3-8e3a6081a193',15000000,'ENW-Lp0s9rN9',false,'PENDING',15000000,'2024-11-07 02:09:44.798532',NULL,'2024-11-07 02:09:44.798552','2024-11-30 19:04:52.473','93f7e345-8f53-4407-9fb0-2da2aba4f493',NULL,'wo-001','ae0f49ca-968b-4d80-9d23-caec353604fd',NULL);

INSERT INTO public.tbl_order_detail (id,bonus,price,quantity,order_id,product_id) VALUES
                                                                                      ('85220845-74e4-4df5-8c83-93926647e2a0',true,5000000.0,3,'1e90e34b-77b1-409b-a95d-37cc041f7fc4','28bfec71-6873-436f-a050-6380c7e1fe97'),
                                                                                      ('c5abf7dd-60d5-44cd-ac7f-fc48568b2793',true,5000000.0,3,'b23abf25-5b02-4692-9af3-8e3a6081a193','28bfec71-6873-436f-a050-6380c7e1fe97');


INSERT INTO public.tbl_notification (id,channel,created_at,data_id,data_type,message,"read",read_at,receiver,receiver_id,"type") VALUES
                                                                                                                                     ('ff704da0-336e-4ad0-a36d-e5cf08f5cef4','SYSTEM','2024-11-07 00:56:24.680982','6e49b972-4e23-4f62-9d91-15392c8d6524','WEDDING_ORGANIZER','New account registered: Eu voluptas iste ut  join our application, click to activate their account',true,'2024-11-07 01:08:34.768135','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('8404f6ed-1773-4b95-810e-ca234b1a19de','SYSTEM','2024-11-07 00:26:37.933495','58e22fd6-554b-4c68-ab37-6d9c0054ae3a','WEDDING_ORGANIZER','New account registered: name join our application, click to activate their account',true,'2024-11-07 01:09:06.196707','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('1700b131-5940-4194-9e96-78a89c213edf','SYSTEM','2024-11-07 00:25:30.662459','f03e3647-3178-4150-81f6-451dd5264924','WEDDING_ORGANIZER','New account registered: Sequi lorem necessit join our application, click to activate their account',true,'2024-11-07 01:09:45.307788','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('756ff26a-1e71-41f9-b992-74a6d43fac53','SYSTEM','2024-11-07 00:24:58.296144','35f6b398-126e-480f-b559-abe289053882','WEDDING_ORGANIZER','New account registered: Voluptas enim magnam join our application, click to activate their account',true,'2024-11-07 01:09:54.118278','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('35ebfa49-6710-401f-a064-e0317c0741df','SYSTEM','2024-11-06 23:56:22.272076','8f4c08fc-eac0-4b72-b62f-521961c6cee7','WEDDING_ORGANIZER','New account registered: Bogor Elegant Weddings join our application, click to activate their account',true,'2024-11-07 01:09:56.587498','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('be5f0495-d73c-4c47-9252-c01af9a02728','SYSTEM','2024-11-06 23:42:32.238406','f7b694c2-60e3-48ad-b8a6-1d329b207846','WEDDING_ORGANIZER','New account registered: Bismillah Sah join our application, click to activate their account',true,'2024-11-07 01:10:03.940488','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('03b2bc10-216a-4fa1-9465-4f577618c6a0','SYSTEM','2024-11-06 23:51:03.585214','77817b27-ec73-4b7e-9b01-f494853f77ea','WEDDING_ORGANIZER','New account registered: Cinta Abadi Wedding Organizer join our application, click to activate their account',true,'2024-11-07 01:10:09.161948','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('03767486-9acb-4602-9f41-ae4ff71de2a3','SYSTEM','2024-11-06 23:53:19.917271','190759a8-19fc-4971-8aa5-14bb54466513','WEDDING_ORGANIZER','New account registered: Surabaya Wedding Dreams join our application, click to activate their account',true,'2024-11-07 01:10:12.448921','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('2e809659-c6e8-4db6-977c-9723c80b7df2','SYSTEM','2024-11-07 01:15:33.932059','831f0c88-5940-4f95-91a6-a54110fc5faf','WEDDING_ORGANIZER','New account registered: Quis ex saepe sed ex join our application, click to activate their account',true,'2024-11-07 01:15:41.984503','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('1a02c021-0244-4e65-9741-9db51e662419','SYSTEM','2024-11-07 02:04:22.219884','0e92fbcb-1377-4b01-81c9-17cbca2e8889','WEDDING_ORGANIZER','New account registered: Voluptate ea est in  join our application, click to activate their account',true,'2024-11-07 02:13:43.681907','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION');
INSERT INTO public.tbl_notification (id,channel,created_at,data_id,data_type,message,"read",read_at,receiver,receiver_id,"type") VALUES
                                                                                                                                     ('0f8c3370-da46-472e-a2cf-b6a6c34f1aa9','SYSTEM','2024-11-07 02:13:26.006636','1e3ea08b-b193-4ebc-a32b-3f65770a1b46','WEDDING_ORGANIZER','New account registered: Ipsum ex aut qui au join our application, click to activate their account',true,'2024-11-07 02:14:27.615381','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('426a3579-e4f8-49b1-bc89-2d7e4d5fdfe7','SYSTEM','2024-11-07 02:01:23.764399','ac66c2e9-48b9-437b-a08a-e8205fab0b30','WEDDING_ORGANIZER','New account registered: Error tempor labore  join our application, click to activate their account',true,'2024-11-07 02:20:48.950205','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('f5a8c031-a6c9-4ced-a3f9-4c849d05a776','SYSTEM','2024-11-07 02:34:05.845504','f216342a-3fff-4252-997e-dce63d9883b8','WEDDING_ORGANIZER','New account registered: Dolor in labore nisi join our application, click to activate their account',true,'2024-11-07 02:34:26.309044','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('e07d79be-8364-4a62-818b-df7af2e85f32','SYSTEM','2024-11-07 02:59:34.467929','53527eb1-3b68-417d-aaf6-c4e81536f03a','WEDDING_ORGANIZER','New account registered: Blanditiis architect join our application, click to activate their account',true,'2024-11-07 02:59:41.913804','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('a8ea43b3-bc1a-4c03-acbf-bcfd5427de79','SYSTEM','2024-11-07 03:01:17.068577','03c9ac18-e1ed-4019-bb98-dcd66eb0947f','WEDDING_ORGANIZER','New account registered: Vitae consectetur ex join our application, click to activate their account',true,'2024-11-07 03:02:40.289298','ADMIN','c35aced7-7d08-4225-9d07-55f9abc10c17','ACCOUNT_REGISTRATION'),
                                                                                                                                     ('6bc7ad6b-e17d-49da-8695-259882883724','SYSTEM','2024-11-07 02:09:44.801629','b23abf25-5b02-4692-9af3-8e3a6081a193','ORDER','New order received: Order receive from customer alwa2, click to check order detail.',true,'2024-11-07 08:26:52.170939','WEDDING_ORGANIZER','fd650833-18b7-4c52-94b5-35aea05467ae','ORDER_RECEIVED'),
                                                                                                                                     ('bc722bff-2f09-4909-906e-f805fea14772','SYSTEM','2024-11-07 02:07:20.331548','1e90e34b-77b1-409b-a95d-37cc041f7fc4','ORDER','New order received: Order receive from customer Alwa, click to check order detail.',true,'2024-11-07 08:27:03.677094','WEDDING_ORGANIZER','fd650833-18b7-4c52-94b5-35aea05467ae','ORDER_RECEIVED');

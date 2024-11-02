-- public.image definition

-- Drop table

-- DROP TABLE public.image;

CREATE TABLE public.image (
	id varchar(255) NOT NULL,
	conten_type varchar(255) NULL,
	"name" varchar(255) NULL,
	"path" varchar(255) NULL,
	"size" int8 NULL,
	CONSTRAINT image_pkey PRIMARY KEY (id)
);


-- public.user_credential definition

-- Drop table

-- DROP TABLE public.user_credential;

CREATE TABLE public.user_credential (
	id varchar(255) NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	active bool NOT NULL,
	email varchar(255) NULL,
	"password" varchar(255) NULL,
	"role" varchar(255) NULL,
	CONSTRAINT user_credential_pkey PRIMARY KEY (id),
	CONSTRAINT user_credential_role_check CHECK (((role)::text = ANY ((ARRAY['ROLE_ADMIN'::character varying, 'ROLE_WO'::character varying])::text[])))
);


-- public.city definition

-- Drop table

-- DROP TABLE public.city;

CREATE TABLE public.city (
	id varchar(255) NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	description varchar(1000) NULL,
	"name" varchar(255) NULL,
	thumbnail_id varchar(255) NULL,
	CONSTRAINT city_pkey PRIMARY KEY (id),
	CONSTRAINT uks7qaqxouo9nq4eohc1kasjkqx UNIQUE (thumbnail_id),
	CONSTRAINT fki65e0j8f5282f836fr8v523io FOREIGN KEY (thumbnail_id) REFERENCES public.image(id)
);


-- public.wedding_organizer definition

-- Drop table

-- DROP TABLE public.wedding_organizer;

CREATE TABLE public.wedding_organizer (
	id varchar(255) NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	address varchar(255) NULL,
	description varchar(1000) NULL,
	"name" varchar(255) NULL,
	nib varchar(255) NULL,
	npwp varchar(255) NULL,
	phone varchar(255) NULL,
	avatar_id varchar(255) NULL,
	city_id varchar(255) NULL,
	user_credential_id varchar(255) NULL,
	CONSTRAINT ukbi1udoovercwyvwl2oke1aq7k UNIQUE (avatar_id),
	CONSTRAINT ukma0bhjhkja4hr7ynl9yqwwk00 UNIQUE (user_credential_id),
	CONSTRAINT wedding_organizer_pkey PRIMARY KEY (id),
	CONSTRAINT fk51od84h9cii6ea2stnanmbor7 FOREIGN KEY (avatar_id) REFERENCES public.image(id),
	CONSTRAINT fk904eml6drp37av8b4cca9h07h FOREIGN KEY (city_id) REFERENCES public.city(id),
	CONSTRAINT fkrmjeq777pawbbjjf0ppr5dwd1 FOREIGN KEY (user_credential_id) REFERENCES public.user_credential(id)
);

INSERT INTO public.image (id,conten_type,"name","path","size") VALUES
	 ('f89e7cab-e17e-42ad-a430-bf0d7e3909ca','image/jpeg','Test_Malang.jpg','src/main/resources/static/images/Test_Malang.jpg',95928),
	 ('beb5927b-06e7-4d6b-8657-86e95a7fa844','image/jpeg','Test_Surabaya.jpg','src/main/resources/static/images/Test_Surabaya.jpg',68681),
	 ('f486fdbc-1228-4298-acc2-7b4747a6a1db','image/jpeg','Test_Bandung.jpg','src/main/resources/static/images/Test_Bandung.jpg',95058),
	 ('7461f789-1b67-4a9d-9f7d-f29a53eaad2e',NULL,NULL,NULL,NULL),
	 ('28482dc4-ed5c-43b3-b24b-cdb695f57050',NULL,NULL,NULL,NULL),
	 ('e0f25def-2961-42ad-9020-afeb354ceaa1',NULL,NULL,NULL,NULL),
	 ('c025136f-7f6e-4c3a-9843-ad27e03869ea',NULL,NULL,NULL,NULL),
	 ('7904e9a5-c132-44cb-840e-4695ec767c1c',NULL,NULL,NULL,NULL),
	 ('a49ff362-e1e5-4318-a075-8a6a1d775b7e',NULL,NULL,NULL,NULL),
	 ('df316288-d7a0-4e1f-bf31-a0b6869e62ff',NULL,NULL,NULL,NULL);
INSERT INTO public.city (id,created_at,deleted_at,updated_at,description,"name",thumbnail_id) VALUES
	 ('city-01','2024-11-02 07:42:00.594865',NULL,NULL,'Discover the enchanting charm of Malang, East Java—where stunning landscapes meet rich cultural heritage. With its cool climate, lush gardens, and vibrant markets, Malang offers the perfect backdrop for your dream wedding. From picturesque venues to delectable local cuisine, create unforgettable memories for you and your guests. Celebrate your love in a truly magical setting!','Kota Malang','f89e7cab-e17e-42ad-a430-bf0d7e3909ca'),
	 ('city-02','2024-11-02 07:43:14.990542',NULL,NULL,'This vibrant East Java metropolis blends rich history with a modern skyline. Enjoy diverse cuisine, historic landmarks, and lively markets. With beautiful venues and a warm coastal climate, Surabaya offers the perfect mix of tradition and contemporary charm for your special day.','Kota Surabaya','beb5927b-06e7-4d6b-8657-86e95a7fa844'),
	 ('city-03','2024-11-02 07:44:09.07985',NULL,NULL,'Celebrate Your Wedding in Bandung! Known for its cool climate and scenic beauty, this West Java city offers lush tea plantations and vibrant arts. With a rich culinary scene, lively markets, and stylish venues, Bandung provides a romantic backdrop for unforgettable moments.','Kota Bandung','f486fdbc-1228-4298-acc2-7b4747a6a1db');
INSERT INTO public.user_credential (id,created_at,deleted_at,updated_at,active,email,"password","role") VALUES
	 ('df7925a6-f5b5-475b-bede-5d8e1eef3766','2024-11-02 07:38:17.0725',NULL,NULL,true,'admin@enigwed.com','$2a$10$wutKqveCtkO/C5bYoSh/be5p/0o9bOsWebAtjbkH6B4q83bHOH2g2','ROLE_ADMIN'),
	 ('f3613144-0f47-4be1-999c-3dfefc954e5a','2024-11-02 07:54:32.051694',NULL,NULL,false,'info@timelessloveevents.com','$2a$10$LM4Axg3g/YHNl4WYqbFHC.8JFo6dJ1vEIb6KPZYybj5SCfR00hZ9a','ROLE_WO'),
	 ('8e13ce80-7205-48d3-bde5-50f706a28c52','2024-11-02 07:49:58.260132',NULL,'2024-11-02 07:57:51.491324',true,'info@elegantweddings.com','$2a$10$7StQ6Cv9Gm2INBONxRkiKOyMRVZnkoNH2BSKELSIE01HxmT/hz/Qi','ROLE_WO'),
	 ('d74e62ea-2fbf-4a1b-830c-25b04c547141','2024-11-02 07:50:31.460947',NULL,'2024-11-02 07:57:56.553096',true,'contact@dreamyweddings.com','$2a$10$YGpb4GhuUZrUebF4hTdKDeV4je0vyg0D5e316wjcZGHmLfgTDXg3m','ROLE_WO'),
	 ('dae88b9c-8ff9-4b86-bead-ec148d55eaa1','2024-11-02 07:51:05.353018',NULL,'2024-11-02 07:58:01.309944',true,'hello@foreveryoursevents.com','$2a$10$BDZcPyh7EulEOr8rcMQZleKp8yKW70Ak2YwiqhoRlwKuvR9NQn05C','ROLE_WO'),
	 ('b7ecaebc-08e3-43b6-af1f-09d6122ac8a1','2024-11-02 07:52:49.442306',NULL,'2024-11-02 07:58:04.633708',true,'info@dreamscapeweddings.com','$2a$10$nXwjOvi2P2qXLhrBNOukb.gJLjLUkIrpiql8JVQ8nj/J4gF0jIUAm','ROLE_WO'),
	 ('3cab3d10-c516-4564-b079-49e6961fcbf7','2024-11-02 07:53:24.90348',NULL,'2024-11-02 07:58:08.052877',true,'contact@eternalblissevents.com','$2a$10$jzyLJqqQLT.g90ji9k6xluvRUg/Q/M5BGNbko2rFHYVhNohZkXZfS','ROLE_WO'),
	 ('6731ed69-c266-4bf9-95d7-9cab934b6c47','2024-11-02 07:53:57.73235',NULL,'2024-11-02 07:58:11.518057',true,'hello@radiantmoments.com','$2a$10$qGcP0F6JXGqHWaHaCcD60eb..xczy2TTSCkmafYxPe0yMlYxUmLFK','ROLE_WO');
INSERT INTO public.wedding_organizer (id,created_at,deleted_at,updated_at,address,description,"name",nib,npwp,phone,avatar_id,city_id,user_credential_id) VALUES
	 ('wo-001','2024-11-02 07:49:58.275749',NULL,NULL,'123 Wedding Lane, Suite 456','A premier wedding planning service that creates unforgettable moments.','Elegant Weddings Co.','0987654321','12.345.678.9-012.345','+621234567890','7461f789-1b67-4a9d-9f7d-f29a53eaad2e','city-01','8e13ce80-7205-48d3-bde5-50f706a28c52'),
	 ('wo-002','2024-11-02 07:50:31.472144',NULL,NULL,'456 Celebration Blvd, Apt 12','Creating magical wedding experiences tailored to your vision.','Dreamy Weddings','1234567890','98.765.432.1-001.234','+628765432109','28482dc4-ed5c-43b3-b24b-cdb695f57050','city-02','d74e62ea-2fbf-4a1b-830c-25b04c547141'),
	 ('wo-003','2024-11-02 07:51:05.363827',NULL,NULL,'789 Bliss Ave, Suite 101','Specializing in bespoke weddings and unforgettable celebrations.','Forever Yours Events','9876543210','11.222.333.4-555.666','+621234567891','e0f25def-2961-42ad-9020-afeb354ceaa1','city-02','dae88b9c-8ff9-4b86-bead-ec148d55eaa1'),
	 ('wo-004','2024-11-02 07:52:49.451921',NULL,NULL,'456 Enchantment Blvd, Suite 205','Creating magical experiences for your special day with personalized touches.','Dreamscape Weddings','1234567895','22.333.444.5-666.777','+621234567892','c025136f-7f6e-4c3a-9843-ad27e03869ea','city-03','b7ecaebc-08e3-43b6-af1f-09d6122ac8a1'),
	 ('wo-005','2024-11-02 07:53:24.91245',NULL,NULL,'321 Celebration Rd, Floor 3','Transforming visions into reality for unforgettable weddings and events.','Eternal Bliss Events','1122334455','33.444.555.6-888.999','+621234567893','7904e9a5-c132-44cb-840e-4695ec767c1c','city-03','3cab3d10-c516-4564-b079-49e6961fcbf7'),
	 ('wo-006','2024-11-02 07:53:57.741664',NULL,NULL,'654 Joy St, Suite 400','Crafting stunning celebrations tailored to your unique love story.','Radiant Moments Weddings','2233445566','44.555.666.7-000.111','+621234567894','a49ff362-e1e5-4318-a075-8a6a1d775b7e','city-01','6731ed69-c266-4bf9-95d7-9cab934b6c47'),
	 ('wo-007','2024-11-02 07:54:32.061325',NULL,NULL,'987 Romance Blvd, Suite 202','Designing elegant weddings and unforgettable celebrations with a personal touch.','Timeless Love Events','3344556677','55.666.777.8-222.333','+621234567895','df316288-d7a0-4e1f-bf31-a0b6869e62ff','city-01','f3613144-0f47-4be1-999c-3dfefc954e5a');

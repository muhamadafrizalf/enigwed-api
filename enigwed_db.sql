-- public.customer definition

-- Drop table

-- DROP TABLE public.customer;

CREATE TABLE public.customer (
                                 id varchar(255) NOT NULL,
                                 email varchar(255) NULL,
                                 "name" varchar(255) NULL,
                                 phone varchar(255) NULL,
                                 CONSTRAINT customer_pkey PRIMARY KEY (id)
);


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
                                        CONSTRAINT user_credential_role_check CHECK (((role)::text = ANY (ARRAY[('ROLE_ADMIN'::character varying)::text, ('ROLE_WO'::character varying)::text])))
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


-- public.wedding_package definition

-- Drop table

-- DROP TABLE public.wedding_package;

CREATE TABLE public.wedding_package (
                                        id varchar(255) NOT NULL,
                                        created_at timestamp(6) NULL,
                                        deleted_at timestamp(6) NULL,
                                        updated_at timestamp(6) NULL,
                                        base_price float8 NULL,
                                        description varchar(10000) NULL,
                                        "name" varchar(255) NULL,
                                        city_id varchar(255) NULL,
                                        wedding_organizer_id varchar(255) NULL,
                                        order_count int4 NULL,
                                        CONSTRAINT wedding_package_pkey PRIMARY KEY (id),
                                        CONSTRAINT fkja7q06xrnm81522ntfkaepid2 FOREIGN KEY (wedding_organizer_id) REFERENCES public.wedding_organizer(id),
                                        CONSTRAINT fkjv0s87tbfhja6p4iq9qj9mr4r FOREIGN KEY (city_id) REFERENCES public.city(id)
);


-- public.wedding_package_image definition

-- Drop table

-- DROP TABLE public.wedding_package_image;

CREATE TABLE public.wedding_package_image (
                                              wedding_package_id varchar(255) NOT NULL,
                                              image_id varchar(255) NOT NULL,
                                              CONSTRAINT fk8ttta6png4odwlyvbw2iu9ywm FOREIGN KEY (image_id) REFERENCES public.image(id),
                                              CONSTRAINT fkjswht8gofvbf8eo2xhcw02m7 FOREIGN KEY (wedding_package_id) REFERENCES public.wedding_package(id)
);


-- public.bonus_package definition

-- Drop table

-- DROP TABLE public.bonus_package;

CREATE TABLE public.bonus_package (
                                      id varchar(255) NOT NULL,
                                      created_at timestamp(6) NULL,
                                      deleted_at timestamp(6) NULL,
                                      updated_at timestamp(6) NULL,
                                      description varchar(1000) NULL,
                                      max_quantity int4 NULL,
                                      min_quantity int4 NULL,
                                      "name" varchar(255) NULL,
                                      price float8 NOT NULL,
                                      wedding_organizer_id varchar(255) NULL,
                                      CONSTRAINT bonus_package_pkey PRIMARY KEY (id),
                                      CONSTRAINT fkr057u2yk1fywo39x467w3lx1w FOREIGN KEY (wedding_organizer_id) REFERENCES public.wedding_organizer(id)
);


-- public.bonus_package_image definition

-- Drop table

-- DROP TABLE public.bonus_package_image;

CREATE TABLE public.bonus_package_image (
                                            bonus_package_id varchar(255) NOT NULL,
                                            image_id varchar(255) NOT NULL,
                                            CONSTRAINT fkeu2dtesf6apmign6kb64l9bj2 FOREIGN KEY (image_id) REFERENCES public.image(id),
                                            CONSTRAINT fkqsfg6psruw68hn8031mu7qw7o FOREIGN KEY (bonus_package_id) REFERENCES public.bonus_package(id)
);


-- public.order_detail definition

-- Drop table

-- DROP TABLE public.order_detail;

CREATE TABLE public.order_detail (
                                     id varchar(255) NOT NULL,
                                     price float8 NOT NULL,
                                     quantity int4 NOT NULL,
                                     bonu_package_id varchar(255) NULL,
                                     order_id varchar(255) NULL,
                                     CONSTRAINT order_detail_pkey PRIMARY KEY (id),
                                     CONSTRAINT fkr113qjsbuhyp0k51l25uidcyh FOREIGN KEY (bonu_package_id) REFERENCES public.bonus_package(id)
);


-- public.bonus_detail definition

-- Drop table

-- DROP TABLE public.bonus_detail;

CREATE TABLE public.bonus_detail (
                                     id varchar(255) NOT NULL,
                                     adjustable bool NOT NULL,
                                     quantity int4 NOT NULL,
                                     bonus_package_id varchar(255) NULL,
                                     wedding_package_id varchar(255) NULL,
                                     CONSTRAINT bonus_detail_pkey PRIMARY KEY (id),
                                     CONSTRAINT fk469fp3j7ub1p0xqijgpr3scen FOREIGN KEY (bonus_package_id) REFERENCES public.bonus_package(id),
                                     CONSTRAINT fkhr0fii8r5400wwkate105nbb9 FOREIGN KEY (wedding_package_id) REFERENCES public.wedding_package(id)
);

INSERT INTO public.image (id,conten_type,"name","path","size") VALUES
                                                                   ('img-bali','image/jpeg','bali.jpg','src/main/resources/static/images/bali.jpg',NULL),
                                                                   ('img-bandung','image/jpeg','bandung.jpg','src/main/resources/static/images/bandung.jpg',NULL),
                                                                   ('img-jakarta','image/jpeg','jakarta.jpg','src/main/resources/static/images/jakarta.jpg',NULL),
                                                                   ('img-malang','image/jpeg','malang.jpg','src/main/resources/static/images/malang.jpg',NULL),
                                                                   ('img-nusa-dua','image/jpeg','nusa-dua.jpg','src/main/resources/static/images/nusa-dua.jpg',NULL),
                                                                   ('img-surabaya','image/jpeg','surabaya.jpg','src/main/resources/static/images/surabaya.jpg',NULL),
                                                                   ('img-yogyakarta','image/jpeg','yogyakarta.jpeg','src/main/resources/static/images/yogyakarta.jpeg',NULL),
                                                                   ('img-wo-001','image/jpeg','wo-001.jpg','src/main/resources/static/images/wo-001.jpg',NULL),
                                                                   ('img-wo-002','image/jpeg','wo-002.jpg','src/main/resources/static/images/wo-002.jpg',NULL),
                                                                   ('img-wo-003','image/jpeg','wo-003.jpg','src/main/resources/static/images/wo-003.jpg',NULL);
INSERT INTO public.image (id,conten_type,"name","path","size") VALUES
                                                                   ('img-wo-004',NULL,NULL,NULL,NULL),
                                                                   ('img-wo-005',NULL,NULL,NULL,NULL),
                                                                   ('img-wo-006',NULL,NULL,NULL,NULL),
                                                                   ('img-wo-007',NULL,NULL,NULL,NULL);

INSERT INTO public.city (id,created_at,deleted_at,updated_at,description,"name",thumbnail_id) VALUES
                                                                                                  ('city-bali',NULL,NULL,NULL,'A tropical paradise for weddings, featuring stunning beaches, lush landscapes, and luxurious venues. Enjoy exquisite cuisine and a blend of traditional and modern elements for a magical celebration. Create unforgettable moments in this enchanting island setting!','Bali','img-bali'),
                                                                                                  ('city-bandung',NULL,NULL,NULL,'A charming city known for its cool climate and beautiful landscapes. Surrounded by tea plantations and volcanic mountains, it offers picturesque venues for weddings. With a vibrant culinary scene, stylish event spaces, and a rich cultural heritage, Bandung provides a romantic backdrop for unforgettable celebrations.','Bandung','img-bandung'),
                                                                                                  ('city-jakarta',NULL,NULL,NULL,'Indonesia''s bustling capital, Jakarta offers a vibrant mix of modernity and tradition. It''s an ideal city for grand weddings with a wide array of luxurious venues, diverse culinary delights, and rich cultural experiences. Celebrate your love amidst the dynamic energy of this metropolis!','Jakarta','img-jakarta'),
                                                                                                  ('city-malang',NULL,NULL,NULL,'Nestled in East Java, Malang is known for its stunning natural beauty and cool climate. With lush gardens and charming venues, it offers a picturesque setting for weddings. Experience delightful local cuisine and a rich cultural heritage for a memorable celebration.','Malang','img-malang'),
                                                                                                  ('city-nusa-dua',NULL,NULL,NULL,'Nusa Dua is a luxurious resort area in Bali, renowned for its pristine beaches and upscale venues. Perfect for destination weddings, it features stunning ocean views and exceptional service. Enjoy a romantic atmosphere for your special day in this idyllic coastal paradise.','Nusa Dua','img-nusa-dua'),
                                                                                                  ('city-surabaya',NULL,NULL,NULL,'Surabaya, the capital of East Java, combines rich history with modernity. With a variety of elegant venues and vibrant culinary options, it offers a unique setting for weddings. Celebrate your love in this dynamic city filled with culture and charm.','Surabaya','img-surabaya'),
                                                                                                  ('city-yogyakarta',NULL,NULL,NULL,'Yogyakarta is the cultural heart of Indonesia, known for its rich heritage and artistic vibes. With beautiful traditional venues and stunning landscapes, it provides a magical backdrop for weddings. Experience authentic cuisine and a warm atmosphere for your special day.','Yogyakarta','img-yogyakarta');

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
                                                                                                                                                              ('wo-001','2024-11-02 07:49:58.275749',NULL,NULL,'123 Wedding Lane, Suite 456','A premier wedding planning service that creates unforgettable moments.','Elegant Weddings Co.','0987654321','12.345.678.9-012.345','+621234567890','img-wo-001','city-bali','8e13ce80-7205-48d3-bde5-50f706a28c52'),
                                                                                                                                                              ('wo-002','2024-11-02 07:50:31.472144',NULL,NULL,'456 Celebration Blvd, Apt 12','Creating magical wedding experiences tailored to your vision.','Dreamy Weddings','1234567890','98.765.432.1-001.234','+628765432109','img-wo-002','city-bandung','d74e62ea-2fbf-4a1b-830c-25b04c547141'),
                                                                                                                                                              ('wo-003','2024-11-02 07:51:05.363827',NULL,NULL,'789 Bliss Ave, Suite 101','Specializing in bespoke weddings and unforgettable celebrations.','Forever Yours Events','9876543210','11.222.333.4-555.666','+621234567891','img-wo-003','city-jakarta','dae88b9c-8ff9-4b86-bead-ec148d55eaa1'),
                                                                                                                                                              ('wo-004','2024-11-02 07:52:49.451921',NULL,NULL,'456 Enchantment Blvd, Suite 205','Creating magical experiences for your special day with personalized touches.','Dreamscape Weddings','1234567895','22.333.444.5-666.777','+621234567892','img-wo-004','city-bandung','b7ecaebc-08e3-43b6-af1f-09d6122ac8a1'),
                                                                                                                                                              ('wo-005','2024-11-02 07:53:24.91245',NULL,NULL,'321 Celebration Rd, Floor 3','Transforming visions into reality for unforgettable weddings and events.','Eternal Bliss Events','1122334455','33.444.555.6-888.999','+621234567893','img-wo-005','city-nusa-dua','3cab3d10-c516-4564-b079-49e6961fcbf7'),
                                                                                                                                                              ('wo-006','2024-11-02 07:53:57.741664',NULL,NULL,'654 Joy St, Suite 400','Crafting stunning celebrations tailored to your unique love story.','Radiant Moments Weddings','2233445566','44.555.666.7-000.111','+621234567894','img-wo-006','city-jakarta','6731ed69-c266-4bf9-95d7-9cab934b6c47'),
                                                                                                                                                              ('wo-007','2024-11-02 07:54:32.061325',NULL,NULL,'987 Romance Blvd, Suite 202','Designing elegant weddings and unforgettable celebrations with a personal touch.','Timeless Love Events','3344556677','55.666.777.8-222.333','+621234567895','img-wo-007','city-bandung','f3613144-0f47-4be1-999c-3dfefc954e5a');

INSERT INTO public.bonus_package (id,created_at,deleted_at,updated_at,description,max_quantity,min_quantity,"name",price,wedding_organizer_id) VALUES
                                                                                                                                                   ('bp-souvenir-001',NULL,NULL,NULL,'Charming keepsakes for your guests, including custom keychains, mini photo frames, and personalized magnets.',500,20,'Elegant Wedding Souvenirs',10000.0,'wo-001'),
                                                                                                                                                   ('bp-food-001',NULL,NULL,NULL,'Delicious snack boxes featuring local delicacies and treats for your guests to enjoy during the celebration.',200,10,'Gourmet Snack Box',15000.0,'wo-001'),
                                                                                                                                                   ('bp-clothes-001',NULL,NULL,NULL,'Stylish outfits for the bride, groom, and groomsmen, including dresses, suits, and accessories.',10,1,'Bridal and Groomsmen Attire Rental',400000.0,'wo-002'),
                                                                                                                                                   ('bp-photo-001',NULL,NULL,NULL,'A fully equipped photo booth with props and backdrops, providing instant prints for guests to take home.',1,1,'Fun Photo Booth Experience',300000.0,'wo-003'),
                                                                                                                                                   ('bp-invitation-001',NULL,NULL,NULL,'Beautifully designed invitations that reflect your wedding theme, including RSVP cards and envelopes.',500,30,'Custom Wedding Invitations',2500.0,'wo-004'),
                                                                                                                                                   ('bp-favors-001',NULL,NULL,NULL,'Custom favors like candles or soaps to thank your guests for attending.',300,50,'Personalized Favors',4000.0,'wo-001'),
                                                                                                                                                   ('bp-catering-001',NULL,NULL,NULL,'Full-service catering with gourmet menu options for your wedding feast.',150,10,'Premium Catering Service',30000.0,'wo-002'),
                                                                                                                                                   ('bp-decor-001',NULL,NULL,NULL,'Stunning floral decorations for tables and venues, tailored to your theme.',20,1,'Elegant Floral Arrangements',50000.0,'wo-003'),
                                                                                                                                                   ('bp-music-001',NULL,NULL,NULL,'Enjoy a live band to entertain your guests during the reception.',2,1,'Live Band Performance',4000000.0,'wo-004'),
                                                                                                                                                   ('bp-photography-001',NULL,NULL,NULL,'Capture your special day with a professional photographer and a full photo album.',1,1,'Professional Wedding Photography',400000.0,'wo-001');
INSERT INTO public.bonus_package (id,created_at,deleted_at,updated_at,description,max_quantity,min_quantity,"name",price,wedding_organizer_id) VALUES
                                                                                                                                                   ('bp-video-001',NULL,NULL,NULL,'High-quality video coverage of your wedding day, including edits and highlights.',1,1,'Wedding Videography',600000.0,'wo-002'),
                                                                                                                                                   ('bp-transport-001',NULL,NULL,NULL,'Rent a luxury car for the bride and groom on their special day.',2,1,'Luxury Car Rental',50000000,'wo-003'),
                                                                                                                                                   ('bp-honeymoon-001',NULL,NULL,NULL,'Special rates for a romantic honeymoon stay at a luxury resort.',1,1,'Honeymoon Package',12000000,'wo-004'),
                                                                                                                                                   ('bp-cake-001',NULL,NULL,NULL,'A beautifully designed wedding cake tailored to your theme and flavors.',1,1,'Custom Wedding Cake',200000.0,'wo-001'),
                                                                                                                                                   ('bp-bar-001',NULL,NULL,NULL,'Unlimited drinks for your guests with a full-service bar at your wedding.',200,30,'Open Bar Service',200000.0,'wo-002'),
                                                                                                                                                   ('bp-rentals-001',NULL,NULL,NULL,'Rent tables, chairs, and linens to create the perfect setup for your reception.',100,10,'Event Furniture Rental',70000.0,'wo-003'),
                                                                                                                                                   ('bp-fireworks-001',NULL,NULL,NULL,'Add a spectacular fireworks show to your wedding celebration.',1,1,'Fireworks Display',500000.0,'wo-004'),
                                                                                                                                                   ('bp-officiant-001',NULL,NULL,NULL,'Professional officiant to perform your wedding ceremony.',1,1,'Wedding Officiant Services',150000.0,'wo-001'),
                                                                                                                                                   ('bp-desserts-001',NULL,NULL,NULL,'A delightful assortment of desserts for guests to enjoy after the meal.',150,20,'Dessert Bar',4000.0,'wo-002'),
                                                                                                                                                   ('bp-stationery-001',NULL,NULL,NULL,'Complete suite of stationery including menus, programs, and place cards.',300,50,'Wedding Stationery Suite',2000.0,'wo-003');
INSERT INTO public.bonus_package (id,created_at,deleted_at,updated_at,description,max_quantity,min_quantity,"name",price,wedding_organizer_id) VALUES
                                                                                                                                                   ('bp-souvenir-002',NULL,NULL,NULL,'Stylish tote bags with your wedding logo as a practical favor for guests.',300,30,'Customized Totes',4000.0,'wo-004'),
                                                                                                                                                   ('bp-food-002',NULL,NULL,NULL,'Delightful mini dessert cups filled with various sweet treats for guests to enjoy.',200,20,'Mini Dessert Cups',3000.0,'wo-001'),
                                                                                                                                                   ('bp-clothes-002',NULL,NULL,NULL,'Rent exquisite accessories like veils, jewelry, and headpieces for the bride.',5,1,'Bridal Accessories Rental',30000.0,'wo-002'),
                                                                                                                                                   ('bp-photo-002',NULL,NULL,NULL,'A kit for creating your own photo booth with props and backdrops.',3,1,'DIY Photo Booth Kit',75000.0,'wo-001'),
                                                                                                                                                   ('bp-invitation-002',NULL,NULL,NULL,'Beautifully designed digital invitations sent via email for modern couples.',500,30,'Digital Invitations',2000.0,'wo-002'),
                                                                                                                                                   ('bp-favors-002',NULL,NULL,NULL,'Eco-friendly seed packets for guests to take home and plant.',300,50,'Seed Packets',8000.0,'wo-001'),
                                                                                                                                                   ('bp-catering-002',NULL,NULL,NULL,'Interactive dinner stations featuring various cuisines for your guests to enjoy.',150,20,'Themed Dinner Stations',20000.0,'wo-002'),
                                                                                                                                                   ('bp-decor-002',NULL,NULL,NULL,'Elegant chair covers and sashes to enhance your reception decor.',200,50,'Chair Covers and Sashes',5000.0,'wo-003'),
                                                                                                                                                   ('bp-music-002',NULL,NULL,NULL,'Professional DJ to keep the dance floor lively all night long.',1,1,'DJ Services',400000.0,'wo-004'),
                                                                                                                                                   ('bp-photography-002',NULL,NULL,NULL,'A pre-wedding photoshoot package to capture your love story.',1,1,'Engagement Photoshoot',200000.0,'wo-001');
INSERT INTO public.bonus_package (id,created_at,deleted_at,updated_at,description,max_quantity,min_quantity,"name",price,wedding_organizer_id) VALUES
                                                                                                                                                   ('bp-video-002',NULL,NULL,NULL,'Stream your wedding live for family and friends who cannot attend in person.',1,1,'Live Streaming Service',100000.0,'wo-002'),
                                                                                                                                                   ('bp-transport-002',NULL,NULL,NULL,'Shuttle services for guests between venues for convenience.',1,1,'Shuttle Services',300000.0,'wo-003'),
                                                                                                                                                   ('bp-honeymoon-002',NULL,NULL,NULL,'An adventure-filled honeymoon package including activities like hiking and snorkeling.',1,1,'Adventure Honeymoon Package',2500000.0,'wo-004'),
                                                                                                                                                   ('bp-cake-002',NULL,NULL,NULL,'A tower of delicious cupcakes, offering a variety of flavors for guests.',1,1,'Cupcake Tower',200000.0,'wo-001'),
                                                                                                                                                   ('bp-bar-002',NULL,NULL,NULL,'A bar featuring custom signature cocktails created just for your wedding.',200,30,'Signature Cocktail Bar',300000.0,'wo-002'),
                                                                                                                                                   ('bp-rentals-002',NULL,NULL,NULL,'Rent high-quality lighting to create a stunning ambiance for your reception.',10,1,'Lighting Equipment Rental',100000.0,'wo-003'),
                                                                                                                                                   ('bp-fireworks-002',NULL,NULL,NULL,'A confetti burst to celebrate the couple during the reception.',1,1,'Confetti Burst',50000.0,'wo-001'),
                                                                                                                                                   ('bp-officiant-002',NULL,NULL,NULL,'Specialized officiant services for intimate elopements.',1,1,'Elopement Officiant',200000.0,'wo-002'),
                                                                                                                                                   ('bp-desserts-002',NULL,NULL,NULL,'Custom gourmet cookies packaged as wedding favors for guests.',300,30,'Gourmet Cookie Favors',3500.0,'wo-001'),
                                                                                                                                                   ('bp-stationery-002',NULL,NULL,NULL,'Beautifully designed thank you cards to send after the wedding.',300,30,'Thank You Cards',2000.0,'wo-002');

INSERT INTO public.wedding_package (id,created_at,deleted_at,updated_at,base_price,description,"name",city_id,wedding_organizer_id,order_count) VALUES
                                                                                                                                                    ('ead72632-0304-4515-9898-19ebef27df86','2024-11-03 16:49:48.075798',NULL,NULL,15000000,'The Elegance Wedding Package is designed for couples seeking a timeless and sophisticated celebration. This comprehensive package includes everything you need for a beautiful wedding day, from the ceremony to the reception, ensuring a seamless experience for you and your guests.','Elegance Wedding Package','city-bali','wo-001',0),
                                                                                                                                                    ('9879505f-52c4-471d-b37c-0608be4dc0a8','2024-11-03 16:51:33.402397',NULL,NULL,20000000,'The Romantic Garden Wedding Package is perfect for couples who dream of an enchanting outdoor celebration. This package includes a stunning floral setup, outdoor seating, and everything you need for a magical ceremony and reception in a beautiful garden setting.','Romantic Garden Wedding Package','city-bali','wo-001',0),
                                                                                                                                                    ('a42b3a40-4544-47f0-96fb-d461f3f64d50','2024-11-03 16:52:07.101122',NULL,NULL,25000000,'The Classic Elegance Wedding Package offers a timeless experience, blending traditional elements with modern sophistication. This package includes venue decor, catering, and all essential services to create an unforgettable day for you and your loved ones.','Classic Elegance Wedding Package','city-bali','wo-001',0),
                                                                                                                                                    ('14856630-3b5b-4e02-9ed4-eadb234e63a9','2024-11-03 16:52:48.10319',NULL,NULL,30000000,'The Dreamy Beach Wedding Package is ideal for couples wanting a romantic seaside celebration. This package features stunning ocean views, beach decor, and all the essentials for a picturesque wedding ceremony and reception on the shore.','Dreamy Beach Wedding Package','city-bali','wo-001',0),
                                                                                                                                                    ('de05ff85-fe9a-4a24-a982-5c32fae2e68b','2024-11-03 16:54:21.02531',NULL,NULL,22000000,'The Charming Rustic Wedding Package is perfect for couples looking to celebrate their love in a cozy, natural setting. This package includes rustic decor, a charming venue, and all the essential services to create a warm and inviting atmosphere for your special day.','Charming Rustic Wedding Package','city-bali','wo-001',0),
                                                                                                                                                    ('a930dc26-10d4-4c3d-9209-1d11342988ca','2024-11-03 16:58:14.265706',NULL,NULL,28000000,'The Elegant Bandung Wedding Package offers a perfect blend of traditional and modern elements for couples looking to celebrate their love in the beautiful city of Bandung. This package includes venue setup, catering, and essential services to create a memorable wedding experience.','Elegant Bandung Wedding Package','city-bandung','wo-002',0),
                                                                                                                                                    ('6bc4fe1c-7724-47f9-8af9-473dfde9348b','2024-11-03 16:58:52.832833',NULL,NULL,25000000,'The Romantic Garden Wedding Package is tailored for couples dreaming of an intimate celebration surrounded by nature. This package includes exquisite decor, a gourmet menu, and all essential services to make your outdoor wedding truly special.','Romantic Garden Wedding Package','city-bandung','wo-002',0),
                                                                                                                                                    ('972d7329-c615-4370-87d2-cfbf9e81dade','2024-11-03 16:59:31.173547',NULL,NULL,32000000,'The Chic Urban Wedding Package is designed for modern couples looking for a stylish and sophisticated celebration in the heart of Bandung. This package includes contemporary decor, a gourmet menu, and premium services to create an unforgettable city wedding experience.','Chic Urban Wedding Package','city-bandung','wo-002',0),
                                                                                                                                                    ('ba8d4062-149f-4a64-887a-fee1d403cf67','2024-11-03 17:00:11.89464',NULL,NULL,27000000,'The Vintage Elegance Wedding Package is perfect for couples who want to celebrate their love with a charming and timeless theme. This package includes beautiful vintage decor, a delightful menu, and essential services for a memorable wedding day.','Vintage Elegance Wedding Package','city-bandung','wo-002',0),
                                                                                                                                                    ('1ef24896-df0a-4b5a-bb85-20d577471332','2024-11-03 17:05:32.265307',NULL,NULL,50000000,'The Lavish City Wedding Package is designed for couples who desire a grand and opulent celebration in Jakarta. This package includes luxurious amenities, exquisite decor, and exceptional services to ensure your wedding is truly unforgettable.','Lavish City Wedding Package','city-jakarta','wo-003',0);
INSERT INTO public.wedding_package (id,created_at,deleted_at,updated_at,base_price,description,"name",city_id,wedding_organizer_id,order_count) VALUES
                                                                                                                                                    ('f5391a02-2301-475c-9ed0-8996521352d0','2024-11-03 17:07:25.892474',NULL,NULL,25000000,'The Memorable Moments Wedding Package is perfect for couples who want to capture every detail of their special day while providing a delightful experience for their guests. This package includes stunning decorations, a fun photo booth, and essential rentals to make your celebration unforgettable.','Memorable Moments Wedding Package','city-jakarta','wo-003',0),
                                                                                                                                                    ('477a69ba-8d94-464d-98ed-aed55ca30501','2024-11-03 17:08:19.526433',NULL,NULL,30000000,'The Elegant Celebration Wedding Package offers a refined and sophisticated experience, perfect for couples looking to host a stylish wedding. This package includes luxurious decor, essential rentals, and a memorable photo booth for guests.','Elegant Celebration Wedding Package','city-jakarta','wo-003',0),
                                                                                                                                                    ('f27cf1d3-b7fe-4f43-a3ee-e62fb3fca2fb','2024-11-03 17:08:51.824904',NULL,NULL,25000000,'The Charming Garden Wedding Package is perfect for couples who dream of a whimsical outdoor celebration. This package includes beautiful floral decorations, comfortable furniture rentals, and a fun photo booth to capture joyful moments.','Charming Garden Wedding Package','city-jakarta','wo-003',0),
                                                                                                                                                    ('675e87b1-2cb9-4ec2-adb8-a2a6bc4ebd1c','2024-11-03 17:12:04.141145',NULL,NULL,30000000,'The Romantic Evening Wedding Package is designed for couples who envision an intimate celebration filled with love and joy. This package includes enchanting live music, beautifully crafted invitations, and delightful wedding favors for your guests.','Romantic Evening Wedding Package','city-bandung','wo-004',0),
                                                                                                                                                    ('dac12d63-a27b-4208-b784-b853f9a34633','2024-11-03 17:13:53.448017',NULL,NULL,25000000,'The Charming Garden Wedding Package is perfect for couples dreaming of a beautiful outdoor celebration. This package includes elegant floral arrangements, a professional DJ to keep the festivities lively, and personalized wedding invitations that match your theme.','Charming Garden Wedding Package','city-bandung','wo-004',0),
                                                                                                                                                    ('31e6654f-d382-4721-a645-9c833c09ca6b','2024-11-03 17:15:37.358183',NULL,NULL,30000000,'The Elegant Evening Soirée Package is designed for couples who want to celebrate their love with a touch of sophistication. This package features enchanting decorations, a live band to entertain guests, and custom wedding invitations to set the tone for your special day.','Elegant Evening Soirée Package','city-bandung','wo-004',0);

INSERT INTO public.bonus_detail (id,adjustable,quantity,bonus_package_id,wedding_package_id) VALUES
                                                                                                 ('843767ef-f7c0-4846-91ee-1f22f046c043',true,30,'bp-souvenir-001','ead72632-0304-4515-9898-19ebef27df86'),
                                                                                                 ('695b7de8-464f-4914-9dc0-a1c9f8e8e68d',true,10,'bp-food-001','ead72632-0304-4515-9898-19ebef27df86'),
                                                                                                 ('aff2f882-9ee1-4466-9026-bee0a71638aa',false,1,'bp-photography-001','ead72632-0304-4515-9898-19ebef27df86'),
                                                                                                 ('fc76d1a5-d093-4e5d-8292-454c1a44c898',false,1,'bp-cake-001','9879505f-52c4-471d-b37c-0608be4dc0a8'),
                                                                                                 ('0df176e7-2289-41d2-a345-a93b0145c4fe',true,50,'bp-food-002','9879505f-52c4-471d-b37c-0608be4dc0a8'),
                                                                                                 ('97da694e-f0a5-489d-8263-981bb503d726',true,100,'bp-favors-001','9879505f-52c4-471d-b37c-0608be4dc0a8'),
                                                                                                 ('06c5c070-7dc3-4b3f-b4fe-42c1a6eb1364',false,1,'bp-officiant-001','a42b3a40-4544-47f0-96fb-d461f3f64d50'),
                                                                                                 ('cb68b3b0-f892-407f-89a6-d169f276b000',false,1,'bp-photo-002','a42b3a40-4544-47f0-96fb-d461f3f64d50'),
                                                                                                 ('5287ee01-0318-4104-821c-f558fbfcdee6',true,40,'bp-desserts-002','a42b3a40-4544-47f0-96fb-d461f3f64d50'),
                                                                                                 ('8f36d4a0-7544-40ff-a8e4-4149c70679ae',false,1,'bp-cake-002','14856630-3b5b-4e02-9ed4-eadb234e63a9');
INSERT INTO public.bonus_detail (id,adjustable,quantity,bonus_package_id,wedding_package_id) VALUES
                                                                                                 ('0ac6ae36-b64a-4cb6-b224-d56cba2054a8',false,1,'bp-fireworks-002','14856630-3b5b-4e02-9ed4-eadb234e63a9'),
                                                                                                 ('deeec537-4652-475b-a6ea-a9b00c3bbf76',true,60,'bp-favors-002','14856630-3b5b-4e02-9ed4-eadb234e63a9'),
                                                                                                 ('38fa51e0-4fe3-4079-866c-739845e56fa3',false,1,'bp-photography-002','de05ff85-fe9a-4a24-a982-5c32fae2e68b'),
                                                                                                 ('323e33bb-bc87-4f9c-a4fa-d1aa917775c7',true,50,'bp-desserts-002','de05ff85-fe9a-4a24-a982-5c32fae2e68b'),
                                                                                                 ('47ae0a31-0307-4bce-9149-cf7bac49ce7b',true,75,'bp-favors-001','de05ff85-fe9a-4a24-a982-5c32fae2e68b'),
                                                                                                 ('be6ee23b-1ad8-4be3-a044-bd8e2fc5024a',true,100,'bp-catering-001','a930dc26-10d4-4c3d-9209-1d11342988ca'),
                                                                                                 ('e8cbdc0f-b393-4a85-a818-b7033a817f85',false,1,'bp-video-001','a930dc26-10d4-4c3d-9209-1d11342988ca'),
                                                                                                 ('10183e3a-55c5-47c9-aa95-431ffd58f53c',true,50,'bp-bar-001','a930dc26-10d4-4c3d-9209-1d11342988ca'),
                                                                                                 ('eb0d57f3-4193-468d-8880-dd6f793c4074',true,2,'bp-clothes-001','a930dc26-10d4-4c3d-9209-1d11342988ca'),
                                                                                                 ('0675c7b3-96c0-4883-aa94-17124aab049e',true,30,'bp-desserts-001','a930dc26-10d4-4c3d-9209-1d11342988ca');
INSERT INTO public.bonus_detail (id,adjustable,quantity,bonus_package_id,wedding_package_id) VALUES
                                                                                                 ('ab7a7d78-fe4c-4d28-8cdd-7faedc130304',true,80,'bp-catering-002','6bc4fe1c-7724-47f9-8af9-473dfde9348b'),
                                                                                                 ('33509cd5-09d2-4d5b-b51b-195a0ce9808e',false,1,'bp-video-002','6bc4fe1c-7724-47f9-8af9-473dfde9348b'),
                                                                                                 ('28625249-6d3f-4aa1-8a23-2880f7d5582a',true,50,'bp-bar-002','6bc4fe1c-7724-47f9-8af9-473dfde9348b'),
                                                                                                 ('64f0ddc9-e21b-4062-a882-61df7f0d2b1b',true,100,'bp-invitation-002','6bc4fe1c-7724-47f9-8af9-473dfde9348b'),
                                                                                                 ('b419baad-fe5c-499c-9a42-c3eadb9ffa2a',false,1,'bp-clothes-002','6bc4fe1c-7724-47f9-8af9-473dfde9348b'),
                                                                                                 ('284a25a9-2867-4157-84f6-5148fd6a156a',true,100,'bp-catering-001','972d7329-c615-4370-87d2-cfbf9e81dade'),
                                                                                                 ('9c069fb9-dea5-4fa4-850e-14344f5987f7',true,70,'bp-bar-001','972d7329-c615-4370-87d2-cfbf9e81dade'),
                                                                                                 ('541eb859-a4e1-49e6-a376-63202d3100dc',false,1,'bp-video-001','972d7329-c615-4370-87d2-cfbf9e81dade'),
                                                                                                 ('9d7d89b3-23e4-4078-b412-e479514e5bca',true,3,'bp-clothes-001','972d7329-c615-4370-87d2-cfbf9e81dade'),
                                                                                                 ('3b339fa3-4e58-432f-95e9-a108c2f5f888',true,100,'bp-stationery-002','972d7329-c615-4370-87d2-cfbf9e81dade');
INSERT INTO public.bonus_detail (id,adjustable,quantity,bonus_package_id,wedding_package_id) VALUES
                                                                                                 ('dedd6401-8b6c-484e-92e7-497b9ae7a8ec',true,90,'bp-catering-002','ba8d4062-149f-4a64-887a-fee1d403cf67'),
                                                                                                 ('5687197a-0066-4b5e-a06d-15425dbeb72f',true,60,'bp-bar-002','ba8d4062-149f-4a64-887a-fee1d403cf67'),
                                                                                                 ('b6ce9be4-bce1-4ee5-9a98-6f3683773151',false,1,'bp-video-002','ba8d4062-149f-4a64-887a-fee1d403cf67'),
                                                                                                 ('d1a2a3a5-6d6c-4121-80e0-f6bf9627f710',false,1,'bp-clothes-002','ba8d4062-149f-4a64-887a-fee1d403cf67'),
                                                                                                 ('494b5535-1f16-43b5-883b-98bacf7923a4',true,40,'bp-desserts-001','ba8d4062-149f-4a64-887a-fee1d403cf67'),
                                                                                                 ('2ae91823-cf58-427e-952c-2c552485c9b8',false,1,'bp-transport-001','1ef24896-df0a-4b5a-bb85-20d577471332'),
                                                                                                 ('e50fcae5-9958-4b3f-bf14-c30b2108679e',true,15,'bp-decor-001','1ef24896-df0a-4b5a-bb85-20d577471332'),
                                                                                                 ('483166af-9c75-4173-99c2-01aa563798df',false,1,'bp-photo-001','1ef24896-df0a-4b5a-bb85-20d577471332'),
                                                                                                 ('6a24d253-c2e9-433e-94ee-18c3bdba7ad0',true,20,'bp-rentals-001','1ef24896-df0a-4b5a-bb85-20d577471332'),
                                                                                                 ('39fe1a28-855b-4372-b502-5ecc38a587fb',true,100,'bp-stationery-001','1ef24896-df0a-4b5a-bb85-20d577471332');
INSERT INTO public.bonus_detail (id,adjustable,quantity,bonus_package_id,wedding_package_id) VALUES
                                                                                                 ('426a5c84-bcf5-4aa7-95e9-24147807a38c',true,1,'bp-transport-002','1ef24896-df0a-4b5a-bb85-20d577471332'),
                                                                                                 ('56ac3341-a5d9-43e1-8f50-9fb9206884b3',false,1,'bp-photo-001','f5391a02-2301-475c-9ed0-8996521352d0'),
                                                                                                 ('c8122dbc-61d9-4d9d-9d76-90a2da7f7a8a',true,10,'bp-decor-001','f5391a02-2301-475c-9ed0-8996521352d0'),
                                                                                                 ('05a010f4-b4ee-4e91-a2da-60b97e3257cd',true,20,'bp-rentals-001','f5391a02-2301-475c-9ed0-8996521352d0'),
                                                                                                 ('1e949c31-6302-4a9d-a2bb-e9826fc12d43',true,100,'bp-decor-002','f5391a02-2301-475c-9ed0-8996521352d0'),
                                                                                                 ('894b8cd6-7490-474c-9e9c-5efa0dd5803c',true,100,'bp-stationery-001','f5391a02-2301-475c-9ed0-8996521352d0'),
                                                                                                 ('04bfc768-b9ce-493d-8d0b-fee7221ecab3',true,15,'bp-decor-001','477a69ba-8d94-464d-98ed-aed55ca30501'),
                                                                                                 ('e4f53e77-a181-4261-a257-4240309cecac',false,1,'bp-photo-001','477a69ba-8d94-464d-98ed-aed55ca30501'),
                                                                                                 ('0c38a9fe-b917-42c4-8f40-77fd51ca32ea',true,25,'bp-rentals-001','477a69ba-8d94-464d-98ed-aed55ca30501'),
                                                                                                 ('853b9191-402b-499c-8b16-0aabcd90a17c',true,150,'bp-decor-002','477a69ba-8d94-464d-98ed-aed55ca30501');
INSERT INTO public.bonus_detail (id,adjustable,quantity,bonus_package_id,wedding_package_id) VALUES
                                                                                                 ('b82bdfa7-d261-4b7e-b558-a8a37c0135d2',true,120,'bp-stationery-001','477a69ba-8d94-464d-98ed-aed55ca30501'),
                                                                                                 ('30fb6f10-9352-4f9a-852a-505a23162cf2',true,10,'bp-decor-001','f27cf1d3-b7fe-4f43-a3ee-e62fb3fca2fb'),
                                                                                                 ('3d5508c6-2550-4fa4-b8d6-3f87ddbc27dc',false,1,'bp-photo-001','f27cf1d3-b7fe-4f43-a3ee-e62fb3fca2fb'),
                                                                                                 ('5b5e2dc6-3992-4633-8939-04d71076556f',true,30,'bp-rentals-001','f27cf1d3-b7fe-4f43-a3ee-e62fb3fca2fb'),
                                                                                                 ('4fd95736-bfe9-4e20-baca-f6e58a803413',true,100,'bp-decor-002','f27cf1d3-b7fe-4f43-a3ee-e62fb3fca2fb'),
                                                                                                 ('7efa7728-0131-4615-bbcb-b7896508bd44',false,1,'bp-transport-002','f27cf1d3-b7fe-4f43-a3ee-e62fb3fca2fb'),
                                                                                                 ('8dc0d3ba-0c6d-48bd-969a-4e72063de3d2',true,100,'bp-invitation-001','675e87b1-2cb9-4ec2-adb8-a2a6bc4ebd1c'),
                                                                                                 ('ebe16d7b-26dd-44c0-b99a-4aaf1156c208',false,1,'bp-music-001','675e87b1-2cb9-4ec2-adb8-a2a6bc4ebd1c'),
                                                                                                 ('9ae7f536-7240-4287-9b7c-34efe6a64a30',true,50,'bp-souvenir-002','675e87b1-2cb9-4ec2-adb8-a2a6bc4ebd1c'),
                                                                                                 ('b1840be1-243c-4d66-aa3f-a3b29d14b449',false,1,'bp-fireworks-001','675e87b1-2cb9-4ec2-adb8-a2a6bc4ebd1c');
INSERT INTO public.bonus_detail (id,adjustable,quantity,bonus_package_id,wedding_package_id) VALUES
                                                                                                 ('69bc7106-66c1-4450-b844-e815d73d4162',true,15,'bp-decor-001','dac12d63-a27b-4208-b784-b853f9a34633'),
                                                                                                 ('d6b1d68b-116c-4853-9ab9-436d0fe2dd36',false,1,'bp-music-002','dac12d63-a27b-4208-b784-b853f9a34633'),
                                                                                                 ('c5c08b31-f995-46d9-8f11-74aff99fceec',true,80,'bp-invitation-001','dac12d63-a27b-4208-b784-b853f9a34633'),
                                                                                                 ('0e0136da-5324-46b4-980b-fa0b275507fb',false,1,'bp-honeymoon-001','dac12d63-a27b-4208-b784-b853f9a34633'),
                                                                                                 ('86d43c11-dcb8-4026-b54e-d72c62394728',true,20,'bp-decor-001','31e6654f-d382-4721-a645-9c833c09ca6b'),
                                                                                                 ('a3b2f17c-09ca-4265-bc03-14b170e911e2',false,1,'bp-music-001','31e6654f-d382-4721-a645-9c833c09ca6b'),
                                                                                                 ('f01aeb3c-64da-4412-94fd-42ff05c12aa6',true,100,'bp-invitation-001','31e6654f-d382-4721-a645-9c833c09ca6b'),
                                                                                                 ('26c9c7b1-db63-42e2-8f02-88ac3437e61b',false,1,'bp-fireworks-001','31e6654f-d382-4721-a645-9c833c09ca6b');



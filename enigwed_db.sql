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


INSERT INTO public.image (id, conten_type, "name", path, "size") VALUES
                                                                     ('img-bali', 'image/jpeg', 'bali.jpg', 'src/main/resources/static/images/bali.jpg', NULL),
                                                                     ('img-bandung', 'image/jpeg', 'bandung.jpg', 'src/main/resources/static/images/bandung.jpg', NULL),
                                                                     ('img-jakarta', 'image/jpeg', 'jakarta.jpg', 'src/main/resources/static/images/jakarta.jpg', NULL),
                                                                     ('img-malang', 'image/jpeg', 'malang.jpg', 'src/main/resources/static/images/malang.jpg', NULL),
                                                                     ('img-nusa-dua', 'image/jpeg', 'nusa-dua.jpg', 'src/main/resources/static/images/nusa-dua.jpg', NULL),
                                                                     ('img-surabaya', 'image/jpeg', 'surabaya.jpg', 'src/main/resources/static/images/surabaya.jpg', NULL),
                                                                     ('img-yogyakarta', 'image/jpeg', 'yogyakarta.jpeg', 'src/main/resources/static/images/yogyakarta.jpeg', NULL),
                                                                     ('img-wo-001', 'image/jpeg', 'wo-001.jpg', 'src/main/resources/static/images/wo-001.jpg', NULL),
                                                                     ('img-wo-002', 'image/jpeg', 'wo-002.jpg', 'src/main/resources/static/images/wo-002.jpg', NULL),
                                                                     ('img-wo-003', 'image/jpeg', 'wo-003.jpg', 'src/main/resources/static/images/wo-003.jpg', NULL),
                                                                     ('img-wo-004', NULL, NULL, NULL, NULL),
                                                                     ('img-wo-005', NULL, NULL, NULL, NULL),
                                                                     ('img-wo-006', NULL, NULL, NULL, NULL),
                                                                     ('img-wo-007', NULL, NULL, NULL, NULL);

INSERT INTO public.city (id, "name", description, thumbnail_id) VALUES
                                                                    ('city-bali', 'Bali', 'A tropical paradise for weddings, featuring stunning beaches, lush landscapes, and luxurious venues. Enjoy exquisite cuisine and a blend of traditional and modern elements for a magical celebration. Create unforgettable moments in this enchanting island setting!', 'img-bali'),
                                                                    ('city-bandung', 'Bandung', 'A charming city known for its cool climate and beautiful landscapes. Surrounded by tea plantations and volcanic mountains, it offers picturesque venues for weddings. With a vibrant culinary scene, stylish event spaces, and a rich cultural heritage, Bandung provides a romantic backdrop for unforgettable celebrations.', 'img-bandung'),
                                                                    ('city-jakarta', 'Jakarta', 'Indonesia''s bustling capital, Jakarta offers a vibrant mix of modernity and tradition. It''s an ideal city for grand weddings with a wide array of luxurious venues, diverse culinary delights, and rich cultural experiences. Celebrate your love amidst the dynamic energy of this metropolis!', 'img-jakarta'),
                                                                    ('city-malang', 'Malang', 'Nestled in East Java, Malang is known for its stunning natural beauty and cool climate. With lush gardens and charming venues, it offers a picturesque setting for weddings. Experience delightful local cuisine and a rich cultural heritage for a memorable celebration.', 'img-malang'),
                                                                    ('city-nusa-dua', 'Nusa Dua', 'Nusa Dua is a luxurious resort area in Bali, renowned for its pristine beaches and upscale venues. Perfect for destination weddings, it features stunning ocean views and exceptional service. Enjoy a romantic atmosphere for your special day in this idyllic coastal paradise.', 'img-nusa-dua'),
                                                                    ('city-surabaya', 'Surabaya', 'Surabaya, the capital of East Java, combines rich history with modernity. With a variety of elegant venues and vibrant culinary options, it offers a unique setting for weddings. Celebrate your love in this dynamic city filled with culture and charm.', 'img-surabaya'),
                                                                    ('city-yogyakarta', 'Yogyakarta', 'Yogyakarta is the cultural heart of Indonesia, known for its rich heritage and artistic vibes. With beautiful traditional venues and stunning landscapes, it provides a magical backdrop for weddings. Experience authentic cuisine and a warm atmosphere for your special day.', 'img-yogyakarta');


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

INSERT INTO public.bonus_package (id, wedding_organizer_id, name, description, price, min_quantity, max_quantity) VALUES
                                                                                                                      ('bp-souvenir-001', 'wo-001', 'Elegant Wedding Souvenirs', 'Charming keepsakes for your guests, including custom keychains, mini photo frames, and personalized magnets.', 10000.00, 20, 500),
                                                                                                                      ('bp-food-001', 'wo-001', 'Gourmet Snack Box', 'Delicious snack boxes featuring local delicacies and treats for your guests to enjoy during the celebration.', 15000.00, 10, 200),
                                                                                                                      ('bp-clothes-001', 'wo-002', 'Bridal and Groomsmen Attire Rental', 'Stylish outfits for the bride, groom, and groomsmen, including dresses, suits, and accessories.', 400000.00, 1, 10),
                                                                                                                      ('bp-photo-001', 'wo-003', 'Fun Photo Booth Experience', 'A fully equipped photo booth with props and backdrops, providing instant prints for guests to take home.', 300000.00, 1, 1),
                                                                                                                      ('bp-invitation-001', 'wo-004', 'Custom Wedding Invitations', 'Beautifully designed invitations that reflect your wedding theme, including RSVP cards and envelopes.', 2500.00, 30, 500),
                                                                                                                      ('bp-favors-001', 'wo-001', 'Personalized Favors', 'Custom favors like candles or soaps to thank your guests for attending.', 4000.00, 50, 300),
                                                                                                                      ('bp-catering-001', 'wo-002', 'Premium Catering Service', 'Full-service catering with gourmet menu options for your wedding feast.', 30000.00, 10, 150),
                                                                                                                      ('bp-decor-001', 'wo-003', 'Elegant Floral Arrangements', 'Stunning floral decorations for tables and venues, tailored to your theme.', 50000.00, 1, 20),
                                                                                                                      ('bp-music-001', 'wo-004', 'Live Band Performance', 'Enjoy a live band to entertain your guests during the reception.', 4000000.00, 1, 2),
                                                                                                                      ('bp-photography-001', 'wo-001', 'Professional Wedding Photography', 'Capture your special day with a professional photographer and a full photo album.', 400000.00, 1, 1),
                                                                                                                      ('bp-video-001', 'wo-002', 'Wedding Videography', 'High-quality video coverage of your wedding day, including edits and highlights.', 600000.00, 1, 1),
                                                                                                                      ('bp-transport-001', 'wo-003', 'Luxury Car Rental', 'Rent a luxury car for the bride and groom on their special day.', 50000000.00, 1, 2),
                                                                                                                      ('bp-honeymoon-001', 'wo-004', 'Honeymoon Package', 'Special rates for a romantic honeymoon stay at a luxury resort.', 12000000.00, 1, 1),
                                                                                                                      ('bp-cake-001', 'wo-001', 'Custom Wedding Cake', 'A beautifully designed wedding cake tailored to your theme and flavors.', 200000.00, 1, 1),
                                                                                                                      ('bp-bar-001', 'wo-002', 'Open Bar Service', 'Unlimited drinks for your guests with a full-service bar at your wedding.', 200000.00, 30, 200),
                                                                                                                      ('bp-rentals-001', 'wo-003', 'Event Furniture Rental', 'Rent tables, chairs, and linens to create the perfect setup for your reception.', 70000.00, 10, 100),
                                                                                                                      ('bp-fireworks-001', 'wo-004', 'Fireworks Display', 'Add a spectacular fireworks show to your wedding celebration.', 500000.00, 1, 1),
                                                                                                                      ('bp-officiant-001', 'wo-001', 'Wedding Officiant Services', 'Professional officiant to perform your wedding ceremony.', 150000.00, 1, 1),
                                                                                                                      ('bp-desserts-001', 'wo-002', 'Dessert Bar', 'A delightful assortment of desserts for guests to enjoy after the meal.', 4000.00, 20, 150),
                                                                                                                      ('bp-stationery-001', 'wo-003', 'Wedding Stationery Suite', 'Complete suite of stationery including menus, programs, and place cards.', 2000.00, 50, 300),
                                                                                                                      ('bp-souvenir-002', 'wo-004', 'Customized Totes', 'Stylish tote bags with your wedding logo as a practical favor for guests.', 4000.00, 30, 300),
                                                                                                                      ('bp-food-002', 'wo-001', 'Mini Dessert Cups', 'Delightful mini dessert cups filled with various sweet treats for guests to enjoy.', 3000.00, 20, 200),
                                                                                                                      ('bp-clothes-002', 'wo-002', 'Bridal Accessories Rental', 'Rent exquisite accessories like veils, jewelry, and headpieces for the bride.', 30000.00, 1, 5),
                                                                                                                      ('bp-photo-002', 'wo-001', 'DIY Photo Booth Kit', 'A kit for creating your own photo booth with props and backdrops.', 75000.00, 1, 3),
                                                                                                                      ('bp-invitation-002', 'wo-002', 'Digital Invitations', 'Beautifully designed digital invitations sent via email for modern couples.', 2000.00, 30, 500),
                                                                                                                      ('bp-favors-002', 'wo-001', 'Seed Packets', 'Eco-friendly seed packets for guests to take home and plant.', 8000.00, 50, 300),
                                                                                                                      ('bp-catering-002', 'wo-002', 'Themed Dinner Stations', 'Interactive dinner stations featuring various cuisines for your guests to enjoy.', 20000.00, 20, 150),
                                                                                                                      ('bp-decor-002', 'wo-003', 'Chair Covers and Sashes', 'Elegant chair covers and sashes to enhance your reception decor.', 5000.00, 50, 200),
                                                                                                                      ('bp-music-002', 'wo-004', 'DJ Services', 'Professional DJ to keep the dance floor lively all night long.', 400000.00, 1, 1),
                                                                                                                      ('bp-photography-002', 'wo-001', 'Engagement Photoshoot', 'A pre-wedding photoshoot package to capture your love story.', 200000.00, 1, 1),
                                                                                                                      ('bp-video-002', 'wo-002', 'Live Streaming Service', 'Stream your wedding live for family and friends who cannot attend in person.', 100000.00, 1, 1),
                                                                                                                      ('bp-transport-002', 'wo-003', 'Shuttle Services', 'Shuttle services for guests between venues for convenience.', 300000.00, 1, 1),
                                                                                                                      ('bp-honeymoon-002', 'wo-004', 'Adventure Honeymoon Package', 'An adventure-filled honeymoon package including activities like hiking and snorkeling.', 2500000.00, 1, 1),
                                                                                                                      ('bp-cake-002', 'wo-001', 'Cupcake Tower', 'A tower of delicious cupcakes, offering a variety of flavors for guests.', 200000.00, 1, 1),
                                                                                                                      ('bp-bar-002', 'wo-002', 'Signature Cocktail Bar', 'A bar featuring custom signature cocktails created just for your wedding.', 300000.00, 30, 200),
                                                                                                                      ('bp-rentals-002', 'wo-003', 'Lighting Equipment Rental', 'Rent high-quality lighting to create a stunning ambiance for your reception.', 100000.00, 1, 10),
                                                                                                                      ('bp-fireworks-002', 'wo-001', 'Confetti Burst', 'A confetti burst to celebrate the couple during the reception.', 50000.00, 1, 1),
                                                                                                                      ('bp-officiant-002', 'wo-002', 'Elopement Officiant', 'Specialized officiant services for intimate elopements.', 200000.00, 1, 1),
                                                                                                                      ('bp-desserts-002', 'wo-001', 'Gourmet Cookie Favors', 'Custom gourmet cookies packaged as wedding favors for guests.', 3500.00, 30, 300),
                                                                                                                      ('bp-stationery-002', 'wo-002', 'Thank You Cards', 'Beautifully designed thank you cards to send after the wedding.', 2000.00, 30, 300);




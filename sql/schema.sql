
CREATE TABLE country(
       country_id serial PRIMARY KEY,
       country_name VARCHAR (255) UNIQUE NOT NULL,
       country_code VARCHAR (5) UNIQUE NOT NULL);


CREATE TABLE user_account(
       user_account_id serial PRIMARY KEY,
       username VARCHAR (355) UNIQUE NOT NULL,
       password VARCHAR (1024) NOT NULL,
       email VARCHAR (355) UNIQUE NOT NULL,
       created_on TIMESTAMP NOT NULL,
       is_admin BOOLEAN NOT NULL default false,
       active BOOLEAN NOT NULL default false,
       last_login TIMESTAMP);

CREATE TABLE user_registration(
       user_registration_id serial PRIMARY KEY,
       user_account_id integer NOT NULL,
       registration_key VARCHAR (255),
       activated BOOLEAN NOT NULL default false,
       CONSTRAINT user_registration_user_account_id_fkey FOREIGN KEY (user_account_id)
       REFERENCES user_account (user_account_id) MATCH SIMPLE 
       ON DELETE CASCADE
       );


CREATE TABLE user_profile(
       user_profile_id serial PRIMARY KEY,
       user_account_id integer NOT NULL,
       first_name VARCHAR (100),
       middle_name VARCHAR (100),
       last_name VARCHAR (100),
       address_line_1 VARCHAR (255),
       address_line_2 VARCHAR (255),
       city VARCHAR (255),
       state_province VARCHAR (255),
       country_code VARCHAR (3),
       CONSTRAINT user_profile_user_account_id_fkey FOREIGN KEY (user_account_id)
       REFERENCES user_account (user_account_id) MATCH SIMPLE 
       ON DELETE CASCADE
       );

CREATE TABLE user_session(
       user_session_id serial PRIMARY KEY,
       user_account_id integer NOT NULL,
       auth_token VARCHAR(64),
       session_expiry TIMESTAMP NOT NULL,
       CONSTRAINT user_session_user_account_id_fkey FOREIGN KEY (user_account_id)
       REFERENCES user_account (user_account_id) MATCH SIMPLE 
       ON DELETE CASCADE
       );


CREATE TABLE user_password_reset(
       user_password_reset_id serial PRIMARY KEY,
       user_account_id integer NOT NULL,
       password_reset_key VARCHAR(32),
       key_expiry TIMESTAMP NOT NULL,
       CONSTRAINT user_password_reset_user_account_id_fkey FOREIGN KEY (user_account_id)
       REFERENCES user_account (user_account_id) MATCH SIMPLE 
       ON DELETE CASCADE
       );


CREATE TABLE site_settings(
       site_settings_id serial PRIMARY KEY,
       active BOOLEAN NOT NULL DEFAULT false,
       site_name VARCHAR (255) NOT NULL,
       site_domain_name VARCHAR (1024) UNIQUE NOT NULL,
       site_link VARCHAR(1024),
       site_email VARCHAR(255),
       site_phone VARCHAR (50),
       enable_email BOOLEAN NOT NULL DEFAULT true,
       mail_host VARCHAR (255),
       mail_user VARCHAR (255),
       mail_password VARCHAR (255),
       mail_port integer,
       mail_ssl BOOLEAN NOT NULL DEFAULT false,
       mail_api_url VARCHAR (255),
       mail_option VARCHAR (10),
       template_folder VARCHAR (255),
       facebook_client_id VARCHAR(100)
       );

CREATE TABLE store_settings(
       store_settings_id serial PRIMARY KEY,
       site_settings_id integer UNIQUE NOT NULL,
       store_name VARCHAR (1024),
       store_description text,
       store_base VARCHAR(5),
       default_currency VARCHAR(10),
       enable_coupons BOOLEAN NOT NULL DEFAULT false,
       enable_guest_checkout BOOLEAN NOT NULL DEFAULT false,
       enable_customer_note BOOLEAN NOT NULL DEFAULT false,
       registration_on_checkout BOOLEAN NOT NULL DEFAULT false,
       default_product_sorting VARCHAR (30),
       shop_display_page VARCHAR (30),
       items_per_page integer NOT NULL DEFAULT 0,
       product_rating BOOLEAN NOT NULL DEFAULT false,
       review_rating_required BOOLEAN NOT NULL DEFAULT false,
       show_verified_buyer_label BOOLEAN NOT NULL DEFAULT false,
       only_verified_buyer_review BOOLEAN NOT NULL DEFAULT false,
       review_alert_mail BOOLEAN NOT NULL DEFAULT false,
       currency_position VARCHAR (30),
       remove_trailing_zeros BOOLEAN NOT NULL DEFAULT false,
       catalog_images_width integer NOT NULL default 150,
       catalog_images_height integer NOT NULL default 150,
       catalog_images_hard_crop BOOLEAN NOT NULL default true,
       single_product_image_width integer NOT NULL default 150,
       single_product_image_height integer NOT NULL default 150,
       single_product_image_hard_crop BOOLEAN NOT NULL default true,
       product_thumbnails_width integer NOT NULL default 150,
       product_thumbnails_height integer NOT NULL default 150,
       product_thumbnails_hard_crop BOOLEAN NOT NULL default true,
       manage_stock BOOLEAN NOT NULL DEFAULT false,
       hold_stock integer NOT NULL DEFAULT 0,
       low_stock_notification BOOLEAN NOT NULL DEFAULT false,
       out_of_stock_notification BOOLEAN NOT NULL DEFAULT false,
       stock_notification_recipient VARCHAR (255),
       low_stock_threshold integer NOT NULL DEFAULT 2,
       out_of_stock_threshold integer NOT NULL DEFAULT 0,
       out_of_stock_visibility BOOLEAN NOT NULL DEFAULT false,
       stock_display_format VARCHAR (30),
       enable_taxes BOOLEAN NOT NULL DEFAULT false,
       tax_included_price BOOLEAN NOT NULL DEFAULT false,
       calculate_tax_based_on VARCHAR(50),
       default_customer_address VARCHAR(20),
       shipping_tax_class VARCHAR(50),
       tax_total_rounding BOOLEAN NOT NULL DEFAULT true,
       checkout_display_tax_price VARCHAR (50),
       CONSTRAINT store_settings_site_settings_id_fkey FOREIGN KEY (site_settings_id)
       REFERENCES site_settings (site_settings_id) MATCH SIMPLE 
       ON DELETE CASCADE
       );

CREATE TABLE item(
       item_id serial PRIMARY KEY,
       item_code_sku VARCHAR (255) UNIQUE NOT NULL,
       item_title VARCHAR (255),
       item_description text,
       item_featured BOOLEAN NOT NULL DEFAULT false,
       item_condition VARCHAR (10),
       item_manage_stock BOOLEAN NOT NULL DEFAULT false,
       item_stock integer,
       item_stock_status BOOLEAN NOT NULL DEFAULT true,
       item_allow_backorders BOOLEAN NOT NULL DEFAULT false,
       item_cost_price DECIMAL(8,2) NOT NULL DEFAULT 0.0,
       item_selling_price DECIMAL(8,2) NOT NULL DEFAULT 0.0,
       item_offer_price DECIMAL(8,2),
       item_shipping_weight DECIMAL(8,2),
       item_shipping_height DECIMAL(8,2),
       item_shipping_length DECIMAL(8,2),			
       item_shipping_width DECIMAL(8,2),
       item_purchase_notes text,
       item_enable_reviews BOOLEAN NOT NULL DEFAULT false       
       );

CREATE TABLE product(
       product_id serial PRIMARY KEY,
       product_sku VARCHAR(255) NOT NULL,
       product_title VARCHAR(1024) NOT NULL,
       product_short_description VARCHAR(255),
       product_long_description text,
       product_url VARCHAR (2048),
       product_meta_keywords VARCHAR (255),
       product_meta_description text,
       product_template VARCHAR (255) NOT NULL default 'product.html',
       product_active BOOLEAN NOT NULL DEFAULT false,
       product_featured BOOLEAN NOT NULL DEFAULT false,
       product_condition VARCHAR (10),
       product_manage_stock BOOLEAN NOT NULL DEFAULT false,
       product_stock integer,
       product_stock_status BOOLEAN NOT NULL DEFAULT true,
       product_allow_backorders BOOLEAN NOT NULL DEFAULT false,
       product_cost_price DECIMAL(8,2) NOT NULL DEFAULT 0.0,
       product_selling_price DECIMAL(8,2) NOT NULL DEFAULT 0.0,
       product_offer_price DECIMAL(8,2),
       product_offer_start_date timestamp NULL,
       product_offer_end_date timestamp NULL,
       product_shipping_weight DECIMAL(8,2),
       product_shipping_height DECIMAL(8,2),
       product_shipping_length DECIMAL(8,2),			
       product_shipping_width DECIMAL(8,2),
       product_purchase_notes text,
       product_enable_reviews BOOLEAN NOT NULL DEFAULT false	    
       )


CREATE TABLE product_type(
       product_type_id serial PRIMARY KEY,
       product_type_name VARCHAR (255) UNIQUE NOT NULL,
       product_type_entity VARCHAR (255) UNIQUE NOT NULL
       );

CREATE TABLE product_type_attribute(
       product_type_attribute_id serial PRIMARY KEY,
       product_type_id integer,
       product_type_attribute_name VARCHAR (255),
       product_type_attribute_label VARCHAR (255),
       product_type_attribute_type VARCHAR (50),
       product_type_attribute_sort_order integer NOT NULL DEFAULT 0,
       CONSTRAINT product_type_attribute_product_type_id_fkey FOREIGN KEY (product_type_id)
       REFERENCES product_type (product_type_id) MATCH SIMPLE 
       ON DELETE CASCADE
       );

CREATE TABLE product_type_attribute_value(
       product_type_attribute_value_id serial PRIMARY KEY,
       product_id integer,
       product_type_attribute_id integer,
       product_type_attribute_value_value VARCHAR (255),
       CONSTRAINT product_type_attribute_value_product_type_attrbute_id_fkey FOREIGN KEY (product_type_attribute_id)
       REFERENCES product_type_attribute (product_type_attribute_id) MATCH SIMPLE 
       ON DELETE CASCADE,
       CONSTRAINT product_type_attribute_value_product_id_fkey FOREIGN KEY (product_id)
       REFERENCES product (product_id) MATCH SIMPLE 
       ON DELETE CASCADE 
       );


CREATE TABLE page(
       page_id serial PRIMARY KEY,
       page_title VARCHAR (1024) NOT NULL,
       page_content text,
       page_content_html text,
       page_meta_keywords VARCHAR (255),
       page_meta_description text,
       page_url VARCHAR (2048),
       site_settings_id integer NOT NULL,
       page_active BOOLEAN NOT NULL DEFAULT true,
       page_template VARCHAR (255) NOT NULL,
       CONSTRAINT page_site_settings_id_fkey FOREIGN KEY (site_settings_id)
       REFERENCES site_settings (site_settings_id) MATCH SIMPLE 
       ON DELETE CASCADE
       );
CREATE INDEX page_url_index on page (page_url);

DROP SCHEMA IF EXISTS admin CASCADE;

CREATE SCHEMA admin;
SET search_path TO admin;

CREATE TABLE admin.user_groups(
	user_group_id SERIAL NOT NULL,
	user_group_name VARCHAR(50) NOT NULL,
	user_group_administrate BOOLEAN DEFAULT false,
	user_group_edit_products BOOLEAN DEFAULT false,
	user_group_delete_products BOOLEAN DEFAULT false,
	user_group_comment BOOLEAN DEFAULT false,
	PRIMARY KEY (user_group_id),
	UNIQUE(user_group_name)
);

INSERT INTO admin.user_groups (user_group_name )
VALUES ('администратор'), ('Конструктор');

CREATE TABLE admin.users(
	user_id SERIAL NOT NULL,
	user_name VARCHAR(50) NOT NULL,
	user_pass VARCHAR(50) NOT NULL,
	user_access_to_db BOOLEAN NOT NULL DEFAULT false,
	user_user_group_id integer NOT NULL,
	PRIMARY KEY (user_id),
	UNIQUE(user_name),
	FOREIGN KEY (user_user_group_id) REFERENCES admin.user_groups(user_group_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO admin.users (user_name, user_pass, user_access_to_db, user_user_group_id )
VALUES 
('Симонов Алексей', '888', true, 1),
('Елькин Николай', '777', true, 2),
('Калинина Ольга', '666', true, 2);


CREATE TABLE admin.settings(
	settings_id SERIAL NOT NULL,
	settings_name VARCHAR(50) NOT NULL,
	settings_user_id integer NOT NULL,
	settings_monitor smallint NOT NULL,
	PRIMARY KEY (settings_id),
	UNIQUE(settings_name),
	FOREIGN KEY (settings_user_id) REFERENCES admin.users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO admin.settings (settings_name, settings_user_id, settings_monitor )
VALUES
('default', 1, 0),
('Симонов Алексей', 1, 1);
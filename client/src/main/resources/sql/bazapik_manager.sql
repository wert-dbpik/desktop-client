-- Создаем
-- схему manager
-- 1)  таблицу ПРЕФИКСЫ                   manager.prefixes
-- 2)  таблицу ПЛОТНОСТИ МАТЕРИАЛОВ       manager.densities
-- 3)  таблицу ПОКРЫТИЯ                   manager.coats
-- 4)  таблицу РАСЧЕТНЫЕ ТИПЫ МАТЕРИАЛОВ  manager.mat_types
-- 5)  таблицу ПАПКИ                      manager.folders
-- 6)  таблицу ТЕХНОЛОГИЧЕСКИЕ ПОМЕТКИ    manager.processes
-- 7)  таблицу ГРУППЫ ЭЛЕМЕНТОВ           manager.part_groups
-- 8)  таблицу ЭЛЕМЕНТЫ                   manager.parts
-- 9)  таблицу ГРУППЫ МАТЕРИАЛОВ          manager.material_groups
-- 10) таблицу МАТЕРИАЛЫ                  manager.materials
-- 11) таблицу ГРУППЫ ИЗДЕЛИЙ             manager.product_groups
-- 12) таблицу ИЗДЕЛИЯ                    manager.products
-- 13) таблицу ДЕТАЛИ                     manager.detail
-- 14) таблицу СБОРКИ                     manager.assembles
-- 15) шаблон НОВОЙ СБОРКИ                manager.new_assembles
-- 16) шаблон ЧЕРТЕЖА                     manager.drafts

CREATE SCHEMA  IF NOT EXISTS manager;
SET search_path TO manager;

CREATE SEQUENCE IF NOT EXISTS parts_id_seq;

--       1)  Префиксы
CREATE TABLE IF NOT EXISTS  manager.prefixes
(
	prefix_id serial NOT NULL,
	prefix_name varchar(10) NOT NULL,
	prefix_note varchar(250),
	PRIMARY KEY (prefix_id),
	UNIQUE(prefix_name)
);

INSERT INTO manager.prefixes (prefix_name)
VALUES ('-'),('ПИК'), ('ЗНАК');

--      2)   Плотности
CREATE TABLE IF NOT EXISTS  manager.densities
(
	density_id serial NOT NULL,
	density_name varchar(50) NOT NULL,
	density_amount decimal(11, 9) NOT NULL,
	density_note varchar(250),
	PRIMARY KEY (density_id),
	UNIQUE(density_name)
);

INSERT INTO manager.densities (density_name, density_amount)
VALUES ('сталь', 0.00000785), ('аллюминий', 0.0000027), ('медь', 0.00000896),
('латунь', 0.0000087), ('текстолит', 0.0000014),  ('резина ТМКЩ', 0.0000012);



--      3)   Покрытия
CREATE TABLE IF NOT EXISTS  manager.coats
(
	coat_id serial NOT NULL,
	coat_name varchar(20) NOT NULL,
	coat_ral varchar(20),
	coat_note varchar(250),
	PRIMARY KEY (coat_id),
	UNIQUE(coat_name)
);

INSERT INTO manager.coats (coat_name, coat_ral)
VALUES  ('б/п', ''), ('ППКсер', '7035');

--    4)    Типы материалов
CREATE TABLE IF NOT EXISTS  manager.mat_types
(
	mat_type_id serial NOT NULL,
	mat_type_name varchar(20) NOT NULL,
	mat_type_note varchar(250),
	PRIMARY KEY (mat_type_id),
	UNIQUE(mat_type_name)
);

INSERT INTO manager.mat_types (mat_type_name)
VALUES ('Листовой'), ('Круглый'),('Профильный'), ('Штучный');

--      5)     Папки
CREATE TABLE IF NOT EXISTS  manager.folders
(
	folder_id serial NOT NULL,
	folder_dec_number varchar(50),
	folder_name varchar(50) NOT NULL,
	folder_note varchar(250),
	PRIMARY KEY (folder_id),
	UNIQUE(folder_dec_number, folder_name)
);

INSERT INTO manager.folders (folder_id, folder_dec_number, folder_name, folder_note)
VALUES (1, '', 'Разложено', '');

ALTER SEQUENCE IF EXISTS manager.folders_folder_id_seq RESTART WITH 2;

--    6)  Технологические указания
CREATE TABLE IF NOT EXISTS  manager.processes
(
	process_id serial NOT NULL,
	process_name varchar(20) NOT NULL,
	process_short_name varchar(20) NOT NULL,
	process_note varchar(250),
	PRIMARY KEY (process_id),
	UNIQUE(process_name)
);

INSERT INTO manager.processes (process_name, process_short_name)
VALUES ('Сварочная', 'Св'), ('Сборочная', 'Сб'), ('Слесарная', 'Сл'),
('Гибочная', 'Гб'), ('КРП', 'К'), ('Панелегибочная', 'ПГ'), ('Штамповка', 'Шт');

--   7)   ГРУППЫ ЭЛЕМЕНТОВ

CREATE TABLE IF NOT EXISTS manager.part_groups
(
	part_group_id serial NOT NULL,
	part_group_parent_id bigint NOT NULL,
	part_group_name varchar(50) NOT NULL,
	UNIQUE(part_group_name),
	PRIMARY KEY (part_group_id)
);

INSERT INTO manager.part_groups (part_group_id, part_group_parent_id, part_group_name)
VALUES  (1, 0, 'Материалы'), (2, 0, 'Детали'), (3, 0, 'Сборки'), (4, 0, 'Изделия');

ALTER SEQUENCE IF EXISTS manager.part_groups_part_group_id_seq RESTART WITH 5;


--    8)   ЭЛЕМЕНТЫ

CREATE TABLE IF NOT EXISTS manager.parts
(
	part_id serial NOT NULL,
	part_part_group_id bigint NOT NULL,
	part_name varchar(50) NOT NULL,
	PRIMARY KEY (part_id),
	FOREIGN KEY (part_part_group_id) REFERENCES manager.part_groups(part_group_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

ALTER SEQUENCE IF EXISTS manager.parts_part_id_seq RESTART WITH 1;


--    9)   Каталог МАТЕРИАЛОВ

CREATE TABLE IF NOT EXISTS manager.material_groups
(
	material_group_id serial NOT NULL,
	material_group_parent_id bigint NOT NULL,
	material_group_name varchar(50) NOT NULL,
	PRIMARY KEY (material_group_id)
);


INSERT INTO manager.material_groups(material_group_id,material_group_parent_id, material_group_name)
VALUES
    (2, 1, 'Листы'),
    (3, 1, 'Круги'),
    (4, 1, 'Профили'),
    (5, 1, 'Трубы'),
    (6, 1, 'Метизы'),
    (7, 1, 'Фурнитура'),
    (8, 1, 'Раскройные');


ALTER SEQUENCE IF EXISTS manager.material_groups_material_group_id_seq RESTART WITH 9;

--    10)     Материалы
CREATE TABLE IF NOT EXISTS  manager.materials
(
	material_id  serial NOT NULL,
	material_part_id bigint NOT NULL,
	material_material_group_id bigint NOT NULL,
	material_name varchar(50) NOT NULL,
	material_mat_type_id bigint NOT NULL,

	material_param_s decimal(6,2),  -- толщина (t), диаметр (D), периметр P
	material_param_x decimal(11,9), -- плотность, масса пог. м. (Mпог.м)

	material_note varchar(250),
	PRIMARY KEY (material_id),
	UNIQUE(material_name),
	FOREIGN KEY (material_part_id) REFERENCES manager.parts(part_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (material_material_group_id) REFERENCES manager.material_groups(material_group_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (material_mat_type_id) REFERENCES manager.mat_types(mat_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

--    11)   Каталог ИЗДЕЛИЙ

CREATE TABLE IF NOT EXISTS manager.product_groups
(
	product_group_id serial NOT NULL,
	product_group_parent_id bigint NOT NULL,
	product_group_name varchar(50) NOT NULL,
	PRIMARY KEY (product_group_id)
);

INSERT INTO manager.product_groups(product_group_id, product_group_parent_id, product_group_name)
VALUES
(2, 1, 'Медицина'),
(3, 1, 'КМО'),
(4, 1, 'ШРУД');

ALTER SEQUENCE IF EXISTS manager.product_groups_product_group_id_seq RESTART WITH 5;


--     12)      Продукты(изделия)
CREATE TABLE IF NOT EXISTS manager.products
(
	product_id  serial  NOT NULL,
	product_part_id bigint NOT NULL,
	product_product_group_id bigint NOT NULL,
	product_dec_number varchar(50) NOT NULL,
	product_name varchar(50) NOT NULL,
	product_folder_id bigint,
	product_link varchar(50),
	product_note varchar(250),
	PRIMARY KEY (product_id),
	UNIQUE(product_dec_number),
	FOREIGN KEY (product_part_id) REFERENCES manager.parts(part_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (product_product_group_id) REFERENCES manager.product_groups(product_group_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (product_folder_id) REFERENCES manager.folders(folder_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

--     13)         Детали

CREATE TABLE IF NOT EXISTS  manager.details
(
	detail_id serial NOT NULL,
	detail_part_id bigint NOT NULL,
	detail_krp varchar(20),
	detail_dec_number varchar(50) not null,
	detail_name varchar(50) NOT NULL,
	detail_coat_id bigint,
	detail_folder_id bigint,
	detail_material_id bigint NOT NULL,
	detail_param_a integer,
	detail_param_b integer,
    detail_draft_id bigint,
	detail_note varchar(250),
	PRIMARY KEY (detail_id),
	UNIQUE(detail_dec_number),
	FOREIGN KEY (detail_part_id) REFERENCES manager.parts(part_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (detail_folder_id) REFERENCES manager.folders(folder_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (detail_draft_id) REFERENCES manager.drafts(draft_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (detail_material_id) REFERENCES manager.materials(material_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (detail_coat_id) REFERENCES manager.coats(coat_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

--    14)          Сборки

CREATE TABLE IF NOT EXISTS manager.assembles
(
	assemble_id  serial NOT NULL,     				-- первичный ключ
	assemble_part_id bigint NOT NULL, 				-- anyPart
	assemble_dec_number varchar(50) not null, -- дец. номер
	assemble_name varchar(30) NOT NULL, 			-- наименование
	assemble_coat_id bigint, 									-- покрытие
	assemble_process_id bigint,       				-- тех. операция
	assemble_folder_id bigint,        				-- папка
    assemble_draft_id bigint,        				-- чертеж
	assemble_note varchar(250),       				-- любые примечания
	PRIMARY KEY (assemble_id),
	UNIQUE(assemble_dec_number),
	FOREIGN KEY (assemble_part_id) REFERENCES manager.parts(part_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (assemble_process_id) REFERENCES manager.processes(process_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (assemble_folder_id) REFERENCES manager.folders(folder_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (assemble_draft_id) REFERENCES manager.drafts(draft_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (assemble_coat_id) REFERENCES manager.coats(coat_id) ON DELETE RESTRICT ON UPDATE CASCADE
);


--     15)  Все сборки в одной таблице

CREATE TABLE IF NOT EXISTS manager.asm_items
(
  asm_id serial NOT NULL,            -- порядковый номер записи
  asm_assemble_id bigint NOT NULL,   -- id сборки в которой участвует запись
	asm_line integer NOT NULL,         -- порядковый номер строки в сборке, назначается вручную
	asm_part_id bigint,                -- ссылка на anyPart
	asm_part_quantity integer,         -- количество элементов в сборке
	PRIMARY KEY (asm_id),
	FOREIGN KEY (asm_assemble_id) REFERENCES manager.assembles(assemble_id) ON DELETE RESTRICT ON UPDATE CASCADE,
	FOREIGN KEY (asm_part_id) REFERENCES manager.parts(part_id) ON DELETE RESTRICT ON UPDATE CASCADE
);
/*
--  16) Чертежи деталей

CREATE TABLE IF NOT EXISTS manager.drafts
(
    draft_id serial NOT NULL,            -- порядковый номер записи
    draft_name varchar(100) NOT NULL,      -- наименование
    draft_user_id bigint NOT NULL, --пользователь
    draft_time timestamptz NOT NULL, --время создания
    draft_note varchar(250), --комментарии и замечания
    FOREIGN KEY (draft_user_id) REFERENCES admin.users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,          ---   примечание
    PRIMARY KEY (draft_id)
);*/

--  16) Чертежи деталей

CREATE TABLE IF NOT EXISTS manager.drafts
(
    draft_id serial NOT NULL,            -- порядковый номер записи
    draft_name varchar(100) NOT NULL,      -- наименование
    draft_user_id bigint NOT NULL, --пользователь
    draft_time timestamptz NOT NULL, --время создания
    draft_note varchar(250), --комментарии и замечания
    FOREIGN KEY (draft_user_id) REFERENCES admin.users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,          ---   примечание
    PRIMARY KEY (draft_id)
);
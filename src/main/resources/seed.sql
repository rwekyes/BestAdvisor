-- =========================================================
-- HOGWARTS-INSPIRED UNIVERSITY SEED DATA
-- 1 Admin
-- 5 Faculty
-- 50 Students
-- 20 Courses
-- =========================================================

-- =========================================================
-- USERS
-- Password for all users:
-- password123
-- =========================================================

INSERT INTO users (id, username, password, user_type, first_name, last_name, email, phone)
VALUES
(1,'mcgonagall','password123','ADMIN','Minerva','McGonagall','mcgonagall@hogwarts.edu','555-1001'),

(2,'snape','password123','FACULTY','Severus','Snape','snape@hogwarts.edu','555-2001'),
(3,'flitwick','password123','FACULTY','Filius','Flitwick','flitwick@hogwarts.edu','555-2002'),
(4,'sprout','password123','FACULTY','Pomona','Sprout','sprout@hogwarts.edu','555-2003'),
(5,'lupin','password123','FACULTY','Remus','Lupin','lupin@hogwarts.edu','555-2004'),
(6,'slughorn','password123','FACULTY','Horace','Slughorn','slughorn@hogwarts.edu','555-2005');

-- =========================================================
-- ADMIN
-- =========================================================

INSERT INTO admins (id, employee_id, access_level)
VALUES
(1,9001,'HEADMASTER');

-- =========================================================
-- FACULTY
-- =========================================================

INSERT INTO faculty
(id, employee_id, department, title, office_location, office_hours, hire_date)
VALUES
(2,'FAC1001','Potions','Professor','Dungeon Office','MWF 10-12','1995-09-01'),
(3,'FAC1002','Charms','Professor','West Tower','TTH 1-3','1998-09-01'),
(4,'FAC1003','Herbology','Professor','Greenhouse 3','MWF 2-4','1997-09-01'),
(5,'FAC1004','Defense Against Dark Arts','Professor','North Tower','TTH 10-12','2000-09-01'),
(6,'FAC1005','Advanced Potions','Professor','Silver Dungeon','MWF 9-11','1992-09-01');

-- =========================================================
-- DEPARTMENTS
-- =========================================================

INSERT INTO departments (id, code, name, chair_id, budget)
VALUES
(1,'POT','Potions',2,250000),
(2,'CHR','Charms',3,180000),
(3,'HERB','Herbology',4,160000),
(4,'DADA','Defense Against Dark Arts',5,220000),
(5,'ALCH','Alchemy',6,300000);

-- =========================================================
-- PROGRAMS
-- =========================================================

INSERT INTO programs
(id, code, name, degree_type, department_id, total_credits_required)
VALUES
(1,'BSMAG','Bachelor of Magical Studies','BS',2,120),
(2,'BSPOT','Bachelor of Potions','BS',1,120),
(3,'BSDEF','Bachelor of Defensive Magic','BS',4,120);

-- =========================================================
-- COURSES (20)
-- =========================================================

INSERT INTO courses
(id, code, name, description, credits, department_id, level)
VALUES
(1,'POT101','Intro to Potions','Basic potion brewing principles',3,1,'UNDERGRADUATE'),
(2,'POT201','Intermediate Potions','Complex potion crafting',4,1,'UNDERGRADUATE'),
(3,'POT301','Advanced Elixirs','Rare magical elixirs',4,1,'GRADUATE'),
(4,'CHR101','Beginning Charms','Foundational wand charms',3,2,'UNDERGRADUATE'),
(5,'CHR201','Defensive Charms','Protective enchantments',4,2,'UNDERGRADUATE'),
(6,'CHR301','Enchantments & Wards','Advanced magical barriers',4,2,'GRADUATE'),
(7,'HERB101','Magical Plants I','Introduction to magical flora',3,3,'UNDERGRADUATE'),
(8,'HERB201','Dangerous Flora','Handling aggressive plants',4,3,'UNDERGRADUATE'),
(9,'HERB301','Rare Botanical Magic','Advanced herbology studies',4,3,'GRADUATE'),
(10,'DADA101','Defense Basics','Basic dark arts defense',3,4,'UNDERGRADUATE'),
(11,'DADA201','Defensive Spellwork','Intermediate combat magic',4,4,'UNDERGRADUATE'),
(12,'DADA301','Dark Creature Defense','Defense against magical beasts',4,4,'GRADUATE'),
(13,'ALCH101','Introduction to Alchemy','Fundamental alchemical theory',3,5,'UNDERGRADUATE'),
(14,'ALCH201','Transmutation Studies','Matter transformation techniques',4,5,'UNDERGRADUATE'),
(15,'ALCH301','Philosopher Stone Theory','Legendary alchemy research',5,5,'GRADUATE'),
(16,'MAG101','History of Magic','Historical magical events',3,2,'UNDERGRADUATE'),
(17,'MAG201','Ancient Runes','Magical rune translation',3,2,'UNDERGRADUATE'),
(18,'MAG301','Divination Theory','Predictive magical arts',3,2,'GRADUATE'),
(19,'FLY101','Broom Flight Basics','Introductory broom flying',2,4,'UNDERGRADUATE'),
(20,'BST101','Care of Magical Creatures','Creature safety and handling',3,3,'UNDERGRADUATE');

-- =========================================================
-- COURSE PREREQUISITES
-- =========================================================

INSERT INTO course_prerequisites (course_id, prerequisite_id, minimum_grade)
VALUES
(2,1,'C'),
(3,2,'B'),
(5,4,'C'),
(6,5,'B'),
(8,7,'C'),
(9,8,'B'),
(11,10,'C'),
(12,11,'B'),
(14,13,'C'),
(15,14,'B');

-- =========================================================
-- SECTIONS
-- =========================================================

INSERT INTO sections
(id, course_id, section_number, semester, `YEAR`, capacity, enrolled,
 faculty_id, room, delivery_method, status)
VALUES
(1,1,'001','FALL',2026,30,0,2,'Dungeon A','IN_PERSON','OPEN'),
(2,2,'001','FALL',2026,25,0,2,'Dungeon B','IN_PERSON','OPEN'),
(3,4,'001','FALL',2026,35,0,3,'Charm Hall','IN_PERSON','OPEN'),
(4,5,'001','FALL',2026,25,0,3,'Charm Hall 2','IN_PERSON','OPEN'),
(5,7,'001','FALL',2026,30,0,4,'Greenhouse 1','IN_PERSON','OPEN'),
(6,8,'001','FALL',2026,20,0,4,'Greenhouse 3','IN_PERSON','OPEN'),
(7,10,'001','FALL',2026,40,0,5,'North Tower','IN_PERSON','OPEN'),
(8,11,'001','FALL',2026,30,0,5,'Defense Arena','IN_PERSON','OPEN'),
(9,13,'001','FALL',2026,25,0,6,'Alchemy Lab','IN_PERSON','OPEN'),
(10,14,'001','FALL',2026,20,0,6,'Alchemy Lab 2','IN_PERSON','OPEN');

-- =========================================================
-- MEETING TIMES
-- =========================================================

INSERT INTO section_meeting_times
(section_id, day_of_week, start_time, end_time, room)
VALUES
(1,'MONDAY','09:00','10:15','Dungeon A'),
(1,'WEDNESDAY','09:00','10:15','Dungeon A'),
(3,'TUESDAY','11:00','12:15','Charm Hall'),
(5,'THURSDAY','13:00','14:15','Greenhouse 1'),
(7,'FRIDAY','10:00','11:15','North Tower');

-- =========================================================
-- STUDENTS (50)
-- IDs 100-149
-- =========================================================

INSERT INTO users
(id, username, password, user_type, first_name, last_name, email, phone)
VALUES
(100,'hpotter','password123','STUDENT','Harry','Potter','harry.potter@hogwarts.edu','555-3001'),
(101,'hgranger','password123','STUDENT','Hermione','Granger','hermione.granger@hogwarts.edu','555-3002'),
(102,'wweasley','password123','STUDENT','Ron','Weasley','ron.weasley@hogwarts.edu','555-3003'),
(103,'dmalfoy','password123','STUDENT','Draco','Malfoy','draco.malfoy@hogwarts.edu','555-3004'),
(104,'llovegood','password123','STUDENT','Luna','Lovegood','luna.lovegood@hogwarts.edu','555-3005'),
(105,'ngbottom','password123','STUDENT','Neville','Longbottom','neville.longbottom@hogwarts.edu','555-3006'),
(106,'cgoyle','password123','STUDENT','Gregory','Goyle','gregory.goyle@hogwarts.edu','555-3007'),
(107,'ccrabbe','password123','STUDENT','Vincent','Crabbe','vincent.crabbe@hogwarts.edu','555-3008'),
(108,'gweasley','password123','STUDENT','Ginny','Weasley','ginny.weasley@hogwarts.edu','555-3009'),
(109,'fweasley','password123','STUDENT','Fred','Weasley','fred.weasley@hogwarts.edu','555-3010'),
(110,'gweasley2','password123','STUDENT','George','Weasley','george.weasley@hogwarts.edu','555-3011'),
(111,'cchang','password123','STUDENT','Cho','Chang','cho.chang@hogwarts.edu','555-3012'),
(112,'cdiggory','password123','STUDENT','Cedric','Diggory','cedric.diggory@hogwarts.edu','555-3013'),
(113,'ppattil','password123','STUDENT','Padma','Patil','padma.patil@hogwarts.edu','555-3014'),
(114,'ppattil2','password123','STUDENT','Parvati','Patil','parvati.patil@hogwarts.edu','555-3015'),
(115,'szabini','password123','STUDENT','Blaise','Zabini','blaise.zabini@hogwarts.edu','555-3016'),
(116,'tfinnigan','password123','STUDENT','Seamus','Finnigan','seamus.finnigan@hogwarts.edu','555-3017'),
(117,'dthomas','password123','STUDENT','Dean','Thomas','dean.thomas@hogwarts.edu','555-3018'),
(118,'akirke','password123','STUDENT','Alicia','Kirke','alicia.kirke@hogwarts.edu','555-3019'),
(119,'kbell','password123','STUDENT','Katie','Bell','katie.bell@hogwarts.edu','555-3020'),

(120,'student20','password123','STUDENT','Cormac','McLaggen','cormac@hogwarts.edu','555-3021'),
(121,'student21','password123','STUDENT','Lavender','Brown','lavender@hogwarts.edu','555-3022'),
(122,'student22','password123','STUDENT','Pansy','Parkinson','pansy@hogwarts.edu','555-3023'),
(123,'student23','password123','STUDENT','Angelina','Johnson','angelina@hogwarts.edu','555-3024'),
(124,'student24','password123','STUDENT','Lee','Jordan','lee@hogwarts.edu','555-3025'),
(125,'student25','password123','STUDENT','Oliver','Wood','oliver@hogwarts.edu','555-3026'),
(126,'student26','password123','STUDENT','Percy','Weasley','percy@hogwarts.edu','555-3027'),
(127,'student27','password123','STUDENT','Colin','Creevey','colin@hogwarts.edu','555-3028'),
(128,'student28','password123','STUDENT','Dennis','Creevey','dennis@hogwarts.edu','555-3029'),
(129,'student29','password123','STUDENT','Susan','Bones','susan@hogwarts.edu','555-3030'),
(130,'student30','password123','STUDENT','Hannah','Abbott','hannah@hogwarts.edu','555-3031'),
(131,'student31','password123','STUDENT','Ernie','Macmillan','ernie@hogwarts.edu','555-3032'),
(132,'student32','password123','STUDENT','Justin','Finch-Fletchley','justin@hogwarts.edu','555-3033'),
(133,'student33','password123','STUDENT','Terry','Boot','terry@hogwarts.edu','555-3034'),
(134,'student34','password123','STUDENT','Michael','Corner','michael@hogwarts.edu','555-3035'),
(135,'student35','password123','STUDENT','Anthony','Goldstein','anthony@hogwarts.edu','555-3036'),
(136,'student36','password123','STUDENT','Roger','Davies','roger@hogwarts.edu','555-3037'),
(137,'student37','password123','STUDENT','Millicent','Bulstrode','millicent@hogwarts.edu','555-3038'),
(138,'student38','password123','STUDENT','Marcus','Flint','marcus@hogwarts.edu','555-3039'),
(139,'student39','password123','STUDENT','Adrian','Pucey','adrian@hogwarts.edu','555-3040'),
(140,'student40','password123','STUDENT','Tracey','Davis','tracey@hogwarts.edu','555-3041'),
(141,'student41','password123','STUDENT','Daphne','Greengrass','daphne@hogwarts.edu','555-3042'),
(142,'student42','password123','STUDENT','Theodore','Nott','theodore@hogwarts.edu','555-3043'),
(143,'student43','password123','STUDENT','Astoria','Greengrass','astoria@hogwarts.edu','555-3044'),
(144,'student44','password123','STUDENT','Barty','Crouch','barty@hogwarts.edu','555-3045'),
(145,'student45','password123','STUDENT','Amos','Diggory','amos@hogwarts.edu','555-3046'),
(146,'student46','password123','STUDENT','Kingsley','Shacklebolt','kingsley@hogwarts.edu','555-3047'),
(147,'student47','password123','STUDENT','Nymphadora','Tonks','tonks@hogwarts.edu','555-3048'),
(148,'student48','password123','STUDENT','Arabella','Figg','figg@hogwarts.edu','555-3049'),
(149,'student49','password123','STUDENT','Newt','Scamander','newt@hogwarts.edu','555-3050');

-- =========================================================
-- STUDENT TABLE
-- =========================================================

INSERT INTO students
(id, student_id, gpa, credits_earned, enrollment_status,
 academic_standing, classification, major, minor, advisor_id)
VALUES
(100,'S100',3.80,45,'ACTIVE','GOOD_STANDING','SOPHOMORE','Defense Against Dark Arts','Charms',5),
(101,'S101',4.00,60,'ACTIVE','GOOD_STANDING','JUNIOR','Charms','Alchemy',3),
(102,'S102',3.20,42,'ACTIVE','GOOD_STANDING','SOPHOMORE','History of Magic','Defense Against Dark Arts',5),
(103,'S103',3.60,55,'ACTIVE','GOOD_STANDING','JUNIOR','Potions','Alchemy',2),
(104,'S104',3.95,48,'ACTIVE','GOOD_STANDING','SOPHOMORE','Divination','Herbology',3),
(105,'S105',3.40,39,'ACTIVE','GOOD_STANDING','SOPHOMORE','Herbology','Care of Magical Creatures',4);

-- =========================================================
-- REMAINING STUDENTS AUTO-GENERATED STYLE
-- =========================================================

INSERT INTO students
(id, student_id, gpa, credits_earned, enrollment_status,
 academic_standing, classification, major, minor, advisor_id)
SELECT
id,
'S' || id,
ROUND(RAND() * 2 + 2, 2),
CAST(RAND() * 60 AS INT),
'ACTIVE',
'GOOD_STANDING',
CASE MOD(id,4)
    WHEN 0 THEN 'FRESHMAN'
    WHEN 1 THEN 'SOPHOMORE'
    WHEN 2 THEN 'JUNIOR'
    ELSE 'SENIOR'
END,
CASE MOD(id,5)
    WHEN 0 THEN 'Potions'
    WHEN 1 THEN 'Charms'
    WHEN 2 THEN 'Herbology'
    WHEN 3 THEN 'Defense Against Dark Arts'
    ELSE 'Alchemy'
END,
'History of Magic',
CASE MOD(id,5)
    WHEN 0 THEN 2
    WHEN 1 THEN 3
    WHEN 2 THEN 4
    WHEN 3 THEN 5
    ELSE 6
END
FROM users
WHERE id BETWEEN 106 AND 149;

-- =========================================================
-- PERMISSIONS
-- =========================================================

INSERT INTO permissions (group_name, feature_code, can_access) VALUES
('STUDENT_BASE', 'REGISTER_COURSES',  TRUE),
('STUDENT_BASE', 'VIEW_GRADES',        TRUE),
('STUDENT_BASE', 'ORDER_TRANSCRIPT',   TRUE),
('STUDENT_BASE', 'VIEW_FINANCIAL',     TRUE),
('STUDENT_BASE', 'VIEW_FINANCIAL_AID', TRUE),
('STUDENT_BASE', 'SEARCH_SECTIONS',    TRUE),
('FACULTY_BASE', 'VIEW_CLASS_ROSTER',    TRUE),
('FACULTY_BASE', 'SUBMIT_GRADES',        TRUE),
('FACULTY_BASE', 'SEARCH_SECTIONS',      TRUE),
('FACULTY_BASE', 'VIEW_STUDENT_PROFILE', TRUE),
('ADMIN_BASE', 'REGISTER_COURSES',     TRUE),
('ADMIN_BASE', 'VIEW_GRADES',          TRUE),
('ADMIN_BASE', 'ORDER_TRANSCRIPT',     TRUE),
('ADMIN_BASE', 'VIEW_FINANCIAL',       TRUE),
('ADMIN_BASE', 'VIEW_FINANCIAL_AID',   TRUE),
('ADMIN_BASE', 'VIEW_CLASS_ROSTER',    TRUE),
('ADMIN_BASE', 'SUBMIT_GRADES',        TRUE),
('ADMIN_BASE', 'SEARCH_SECTIONS',      TRUE),
('ADMIN_BASE', 'VIEW_BUDGET',          TRUE),
('ADMIN_BASE', 'VIEW_STUDENT_PROFILE', TRUE),
('ADMIN_BASE', 'SYSTEM_MANAGEMENT',    TRUE);

-- =========================================================
-- USER ROLES
-- =========================================================

INSERT INTO user_roles (user_id, role_name)
SELECT id, 'STUDENT_BASE'
FROM students;

INSERT INTO user_roles (user_id, role_name)
SELECT id, 'FACULTY_BASE'
FROM faculty;

INSERT INTO user_roles (user_id, role_name)
VALUES
(1,'ADMIN_BASE');

-- =========================================================
-- SAMPLE ENROLLMENTS
-- =========================================================

INSERT INTO enrollments
(student_id, section_id, status, grade)
VALUES
(100,1,'ENROLLED',NULL),
(100,3,'ENROLLED',NULL),
(101,3,'ENROLLED',NULL),
(101,9,'ENROLLED',NULL),
(102,7,'ENROLLED',NULL),
(103,1,'ENROLLED',NULL),
(104,5,'ENROLLED',NULL),
(105,5,'ENROLLED',NULL);

-- =========================================================
-- PASSWORD POLICY
-- =========================================================

INSERT INTO password_policies
(policy_name, min_length, require_uppercase,
 require_lowercase, require_digit, require_special,
 max_age_days, history_count, is_active)
VALUES
('DEFAULT_POLICY',8,TRUE,TRUE,TRUE,FALSE,90,5,TRUE);

-- =========================================================
-- REGISTRATION PERIOD
-- =========================================================

INSERT INTO registration_periods
(semester, `YEAR`, open_date, close_date,
 late_registration_end, current_state)
VALUES
('FALL',2026,
 TIMESTAMP '2026-08-01 00:00:00',
 TIMESTAMP '2026-08-31 23:59:59',
 TIMESTAMP '2026-09-07 23:59:59',
 'OPEN');

-- =========================================================
-- GRADING PERIOD
-- =========================================================

INSERT INTO grading_periods
(semester, `YEAR`, open_date, close_date, current_state)
VALUES
('FALL',2026,
 TIMESTAMP '2026-12-01 00:00:00',
 TIMESTAMP '2026-12-20 23:59:59',
 'NOT_OPEN');
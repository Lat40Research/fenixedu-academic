--  SQL file representing changes to the functionalities model
--  Generated at Mon Apr 23 19:38:32 WEST 2007
--  DO NOT EDIT THIS FILE, run the generating script instead

--  Preamble
SET AUTOCOMMIT = 0;

START TRANSACTION;

-- 
--  Inserting new functionalities
-- 

--  ID: 116867 UUID: 'e9ab1a2b-cfd2-4c6d-9476-27c7c3caf4c6'
INSERT INTO `ACCESSIBLE_ITEM` (`UUID`, `OJB_CONCRETE_CLASS`, `KEY_ROOT_DOMAIN_OBJECT`, `KEY_PARENT`, `KEY_MODULE`, `KEY_AVAILABILITY_POLICY`, `NAME`, `TITLE`, `DESCRIPTION`, `PATH`, `PREFIX`, `RELATIVE`, `ENABLED`, `PARAMETERS`, `ORDER_IN_MODULE`, `VISIBLE`, `MAXIMIZED`, `PRINCIPAL`, `KEY_SUPERIOR_SECTION`, `SECTION_ORDER`, `KEY_SITE`, `LAST_MODIFIED_DATE_YEAR_MONTH_DAY`, `KEY_SECTION`, `ITEM_ORDER`, `INFORMATION`, `KEY_FUNCTIONALITY`) SELECT 'e9ab1a2b-cfd2-4c6d-9476-27c7c3caf4c6', 'net.sourceforge.fenixedu.domain.functionalities.ConcreteFunctionality', 1, NULL, `ID_INTERNAL`, NULL, 'en6:Eventspt7:Eventos', 'en6:Eventspt7:Eventos', NULL, '/viewResearchUnitSite.do?method=showEvents', NULL, 1, 1, 'siteID', 1, 1, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL FROM `ACCESSIBLE_ITEM` WHERE `UUID` = 'aade3f06-9faf-49cc-a28b-d2cf136f8469';

--  ID: 116868 UUID: '8481d1a7-1b95-43f6-b091-e0d582aa86e5'
INSERT INTO `ACCESSIBLE_ITEM` (`UUID`, `OJB_CONCRETE_CLASS`, `KEY_ROOT_DOMAIN_OBJECT`, `KEY_PARENT`, `KEY_MODULE`, `KEY_AVAILABILITY_POLICY`, `NAME`, `TITLE`, `DESCRIPTION`, `PATH`, `PREFIX`, `RELATIVE`, `ENABLED`, `PARAMETERS`, `ORDER_IN_MODULE`, `VISIBLE`, `MAXIMIZED`, `PRINCIPAL`, `KEY_SUPERIOR_SECTION`, `SECTION_ORDER`, `KEY_SITE`, `LAST_MODIFIED_DATE_YEAR_MONTH_DAY`, `KEY_SECTION`, `ITEM_ORDER`, `INFORMATION`, `KEY_FUNCTIONALITY`) SELECT '8481d1a7-1b95-43f6-b091-e0d582aa86e5', 'net.sourceforge.fenixedu.domain.functionalities.ConcreteFunctionality', 1, NULL, `ID_INTERNAL`, NULL, 'en13:Announcementspt8:Anúncios', 'en13:Announcementspt8:Anúncios', NULL, '/viewResearchUnitSite.do?method=showAnnouncements', NULL, 1, 1, 'siteID', 2, 1, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL FROM `ACCESSIBLE_ITEM` WHERE `UUID` = 'aade3f06-9faf-49cc-a28b-d2cf136f8469';

COMMIT;
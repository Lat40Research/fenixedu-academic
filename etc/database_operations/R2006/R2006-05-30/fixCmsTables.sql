alter table CMS_EXECUTION_COURSE_WEBSITE change column CREATION_DATE CREATION_DATE_DATE_TIME timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;

alter table CMS_WEBSITE_SECTION change column CREATION_DATE CREATION_DATE_DATE_TIME timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;

alter table CMS_FUNCTIONALITY_LINK change column CREATION_DATE CREATION_DATE_DATE_TIME timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;

alter table CMS_WEBSITE_ITEM change column CREATION_DATE CREATION_DATE_DATE_TIME timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;
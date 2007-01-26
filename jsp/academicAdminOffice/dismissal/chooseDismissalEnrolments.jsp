<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<html:xhtml/>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<em><bean:message key="label.academicAdminOffice" bundle="ACADEMIC_OFFICE_RESOURCES"/></em>
<h2><bean:message key="label.studentDismissal.management" bundle="ACADEMIC_OFFICE_RESOURCES"/></h2>
<br/>
<html:messages id="message" message="true" bundle="ACADEMIC_OFFICE_RESOURCES">
	<p>
		<span class="error"><!-- Error messages go here --><bean:write name="message" /></span>
	</p>
	<br/>
</html:messages>
<fr:view name="dismissalBean" schema="DismissalBean.view">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle4" />
	</fr:layout>
</fr:view>
<br/>
<h3><u><bean:message key="label.studentDismissal.step.one" bundle="ACADEMIC_OFFICE_RESOURCES"/></u> &gt; <bean:message key="label.studentDismissal.step.two" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
<br/>
<fr:form action="/studentDismissals.do">
	<html:hidden property="method" value="chooseEquivalents"/>
	
	<fr:edit id="dismissalBean" name="dismissalBean" visible="false"/>

	<h3><bean:message key="label.studentDismissal.externalEnrolments" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
	<logic:notEmpty name="dismissalBean" property="externalEnrolments">	
		<fr:edit id="externalEnrolments" name="dismissalBean" property="externalEnrolments" schema="student.Dismissal.choose.external.enrolments">
			<fr:layout name="tabular-editable">
				<fr:property name="sortBy" value="externalEnrolment.name"/>
				<fr:property name="classes" value="tstyle4"/>
			</fr:layout>
		</fr:edit>		
	</logic:notEmpty>	
	<logic:empty name="dismissalBean" property="externalEnrolments">
		<br/>				
		<em><bean:message key="label.studentDismissal.externalEnrolments.empty" bundle="ACADEMIC_OFFICE_RESOURCES"/></em>
		<br/>				
	</logic:empty>
	<br/>
	
	<h3><bean:message key="label.studentDismissal.internalEnrolments" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
	<logic:notEmpty name="dismissalBean" property="enrolments">
		<fr:edit id="internalEnrolments" name="dismissalBean" property="enrolments" schema="student.Dismissal.choose.internal.enrolments">
			<fr:layout name="tabular-editable">
				<fr:property name="sortBy" value="enrolment.studentCurricularPlan.startDate,enrolment.executionPeriod,enrolment.name"/>
				<fr:property name="classes" value="tstyle4"/>
			</fr:layout>
		</fr:edit>
	</logic:notEmpty>
	<logic:empty name="dismissalBean" property="enrolments">
		<em><bean:message key="label.studentDismissal.internalEnrolments.empty" bundle="ACADEMIC_OFFICE_RESOURCES"/></em>
		<br/>
		<br/>				
	</logic:empty>
	<br/>
	<html:submit onclick="this.form.method.value='chooseEquivalents'; return true;"><bean:message key="button.submit" bundle="ACADEMIC_OFFICE_RESOURCES"/></html:submit>	
	<html:submit onclick="this.form.method.value='back'; return true;"><bean:message key="button.back" bundle="ACADEMIC_OFFICE_RESOURCES"/></html:submit>
</fr:form>
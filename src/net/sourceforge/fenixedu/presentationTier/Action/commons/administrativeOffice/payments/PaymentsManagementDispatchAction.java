/*
 * Created on Jun 26, 2006
 */
package net.sourceforge.fenixedu.presentationTier.Action.commons.administrativeOffice.payments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidArgumentsServiceException;
import net.sourceforge.fenixedu.dataTransferObject.accounting.CreateReceiptBean;
import net.sourceforge.fenixedu.dataTransferObject.accounting.PaymentsManagementDTO;
import net.sourceforge.fenixedu.dataTransferObject.accounting.SelectableEntryBean;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.accounting.Entry;
import net.sourceforge.fenixedu.domain.accounting.Event;
import net.sourceforge.fenixedu.domain.accounting.PaymentMode;
import net.sourceforge.fenixedu.domain.accounting.Receipt;
import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.domain.candidacy.Candidacy;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.exceptions.DomainExceptionWithLabelFormatter;
import net.sourceforge.fenixedu.domain.organizationalStructure.Party;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.person.IDDocumentType;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;
import net.sourceforge.fenixedu.presentationTier.Action.sop.utils.ServiceUtils;
import net.sourceforge.fenixedu.renderers.components.state.IViewState;
import net.sourceforge.fenixedu.renderers.utils.RenderUtils;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.joda.time.DateTime;

public abstract class PaymentsManagementDispatchAction extends FenixDispatchAction {

    public ActionForward prepareSearchPerson(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	return mapping.findForward("searchPerson");
    }

    public ActionForward searchPersonByCandidacyNumber(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	final DynaActionForm form = (DynaActionForm) actionForm;

	final Integer candidacyNumber = getCandidacyNumber(form);
	if (candidacyNumber == null) {
	    addActionMessage(request, "error.administrativeOffice.payments.invalid.candidacyNumber");
	    form.set("candidacyNumber", candidacyNumber);
	    return prepareSearchPerson(mapping, actionForm, request, response);
	}
	final Candidacy candidacy = Candidacy.readByCandidacyNumber(candidacyNumber);
	if (candidacy == null) {
	    addActionMessage(request,
		    "error.administrativeOffice.payments.invalid.candidacyNumber.withNumber",
		    candidacyNumber.toString());
	    form.set("candidacyNumber", candidacyNumber.toString());
	    return prepareSearchPerson(mapping, actionForm, request, response);
	}

	request.setAttribute("person", candidacy.getPerson());

	return findMainForward(mapping);
    }

    public ActionForward searchPersonByUsername(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	final DynaActionForm form = (DynaActionForm) actionForm;
	final String username = (String) form.get("username");
	final Person person = getPersonByUsername(username);

	if (person == null) {
	    addActionMessage(request, "error.administrativeOffice.payments.person.not.found");
	    return prepareSearchPerson(mapping, actionForm, request, response);
	}

	request.setAttribute("person", person);
	return findMainForward(mapping);
    }

    public ActionForward searchPersonByDocumentIDandDocumentType(ActionMapping mapping,
	    ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) {

	final DynaActionForm form = (DynaActionForm) actionForm;
	final IDDocumentType documentType = IDDocumentType.valueOf(form.getString("documentType"));
	final String documentNumber = (String) form.get("documentNumber");

	final Person person = getPersonByDocumentNumberIdAndDocumentType(documentNumber, documentType);

	if (person == null) {
	    addActionMessage(request, "error.administrativeOffice.payments.person.not.found");
	    return prepareSearchPerson(mapping, actionForm, request, response);
	}

	request.setAttribute("person", person);
	return findMainForward(mapping);
    }

    protected Integer getCandidacyNumber(final DynaActionForm form) {
	final String candidacyNumberString = (String) form.get("candidacyNumber");
	try {
	    return (candidacyNumberString != null && !candidacyNumberString.equals("")) ? Integer
		    .valueOf(candidacyNumberString) : null;
	} catch (NumberFormatException ex) {
	    return null;
	}
    }

    protected Person getPersonByUsername(String username) {
	return Person.readPersonByUsername(username);
    }

    protected Person getPersonByDocumentNumberIdAndDocumentType(String documentNumber,
	    IDDocumentType documentType) {
	return Person.readByDocumentIdNumberAndIdDocumentType(documentNumber, documentType);
    }

    protected Person getPersonByCandidacyNumber(Integer candidacyNumber) {
	final Candidacy candidacy = Candidacy.readByCandidacyNumber(candidacyNumber);
	return (candidacy != null) ? candidacy.getPerson() : null;
    }

    protected PaymentsManagementDTO searchNotPayedEventsForPerson(HttpServletRequest request,
	    Person person, boolean withInstallments) {

	final PaymentsManagementDTO paymentsManagementDTO = new PaymentsManagementDTO(person);
	for (final Event event : person.getNotPayedEventsPayableOn(getAdministrativeOffice(request),
		withInstallments)) {
	    paymentsManagementDTO.addEntryDTOs(event.calculateEntries());
	}

	return paymentsManagementDTO;
    }

    public ActionForward showEvents(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	request.setAttribute("paymentsManagementDTO", searchNotPayedEventsForPerson(request,
		getPerson(request), false));

	return mapping.findForward("showEvents");
    }

    public ActionForward showEventsWithInstallments(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {

	request.setAttribute("paymentsManagementDTO", searchNotPayedEventsForPerson(request,
		getPerson(request), true));

	return mapping.findForward("showEventsWithInstallments");
    }

    public ActionForward showPaymentsWithoutReceipt(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {

	final Person person = getPerson(request);
	final CreateReceiptBean receiptBean = new CreateReceiptBean();
	final IViewState viewState = RenderUtils.getViewState("entriesToSelect");
	final Collection<Entry> entriesToSelect = (Collection<Entry>) ((viewState != null) ? viewState
		.getMetaObject().getObject() : null);

	receiptBean.setPerson(person);
	receiptBean.setEntries(getSelectableEntryBeans(person
		.getPaymentsWithoutReceiptByAdministrativeOffice(getAdministrativeOffice(request)),
		(entriesToSelect != null) ? entriesToSelect : new HashSet<Entry>()));

	request.setAttribute("createReceiptBean", receiptBean);

	return mapping.findForward("showPaymentsWithoutReceipt");
    }

    public ActionForward confirmCreateReceipt(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	final CreateReceiptBean createReceiptBean = (CreateReceiptBean) RenderUtils.getViewState(
		"createReceiptBean").getMetaObject().getObject();

	if (createReceiptBean.getContributorParty() == null) {
	    final Party contributor = Party.readByContributorNumber(createReceiptBean
		    .getContributorNumber());
	    if (contributor == null) {
		addActionMessage(request,
			"error.administrativeOffice.payments.receipt.contributor.does.not.exist");

		request.setAttribute("personId", createReceiptBean.getPerson().getIdInternal());
		return showPaymentsWithoutReceipt(mapping, actionForm, request, response);

	    } else {
		createReceiptBean.setContributorParty(contributor);
	    }
	}

	if (createReceiptBean.getSelectedEntries().isEmpty()) {
	    addActionMessage(request,
		    "error.administrativeOffice.payments.receipt.entries.selection.is.required");

	    request.setAttribute("personId", createReceiptBean.getPerson().getIdInternal());
	    return showPaymentsWithoutReceipt(mapping, actionForm, request, response);
	}

	request.setAttribute("createReceiptBean", createReceiptBean);
	return mapping.findForward("confirmCreateReceipt");
    }

    public ActionForward createReceipt(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) throws FenixFilterException,
	    FenixServiceException {

	final CreateReceiptBean createReceiptBean = (CreateReceiptBean) RenderUtils.getViewState(
		"createReceiptBeanConfirm").getMetaObject().getObject();

	try {
	    final Receipt receipt = (Receipt) ServiceUtils.executeService(getUserView(request),
		    "CreateReceipt", new Object[] { getUserView(request).getPerson().getEmployee(),
			    createReceiptBean.getPerson(), createReceiptBean.getContributorParty(),
			    createReceiptBean.getSelectedEntries() });

	    request.setAttribute("personId", receipt.getPerson().getIdInternal());
	    request.setAttribute("receiptID", receipt.getIdInternal());

	    return prepareShowReceipt(mapping, form, request, response);

	} catch (DomainException ex) {

	    addActionMessage(request, ex.getKey(), ex.getArgs());
	    request.setAttribute("createReceiptBean", createReceiptBean);
	    return mapping.findForward("confirmCreateReceipt");
	}
    }

    private List<SelectableEntryBean> getSelectableEntryBeans(final Set<Entry> entries,
	    final Collection<Entry> entriesToSelect) {
	final List<SelectableEntryBean> selectableEntryBeans = new ArrayList<SelectableEntryBean>();
	for (final Entry entry : entries) {
	    selectableEntryBeans.add(new SelectableEntryBean(entriesToSelect.contains(entry) ? true
		    : false, entry));
	}
	return selectableEntryBeans;
    }

    public ActionForward showReceipts(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	request.setAttribute("person", getPerson(request));
	request.setAttribute("receiptsForAdministrativeOffice", getPerson(request)
		.getReceiptsByAdministrativeOffice(getAdministrativeOffice(request)));

	return mapping.findForward("showReceipts");
    }

    public ActionForward preparePayment(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {

	return internalPreparePayment(mapping, request, "showEvents");

    }

    public ActionForward preparePaymentWithInstallments(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {

	return internalPreparePayment(mapping, request, "showEventsWithInstallments");

    }

    private ActionForward internalPreparePayment(ActionMapping mapping, HttpServletRequest request,
	    String errorForward) {
	final PaymentsManagementDTO paymentsManagementDTO = (PaymentsManagementDTO) RenderUtils
		.getViewState("paymentsManagementDTO").getMetaObject().getObject();

	paymentsManagementDTO.setPaymentDate(new DateTime());

	request.setAttribute("paymentsManagementDTO", paymentsManagementDTO);

	if (paymentsManagementDTO.getSelectedEntries().isEmpty()) {
	    addActionMessage(request,
		    "error.administrativeOffice.payments.payment.entries.selection.is.required");
	    return mapping.findForward(errorForward);

	} else {
	    return mapping.findForward("preparePayment");
	}
    }

    public ActionForward doPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws FenixFilterException, FenixServiceException {

	final PaymentsManagementDTO paymentsManagementDTO = (PaymentsManagementDTO) RenderUtils
		.getViewState("paymentsManagementDTO-edit").getMetaObject().getObject();

	if (paymentsManagementDTO.getSelectedEntries().isEmpty()) {

	    addActionMessage(request,
		    "error.administrativeOffice.payments.payment.entries.selection.is.required");
	    request.setAttribute("paymentsManagementDTO", paymentsManagementDTO);

	    return mapping.findForward("showEvents");
	}

	try {
	    final Collection<Entry> resultingEntries = (Collection<Entry>) ServiceUtils.executeService(
		    getUserView(request), "CreatePaymentsForEvents", new Object[] {
			    getUserView(request).getPerson().getUser(),
			    paymentsManagementDTO.getSelectedEntries(), PaymentMode.CASH,
			    paymentsManagementDTO.isDifferedPayment(),
			    paymentsManagementDTO.getPaymentDate() });

	    request.setAttribute("person", paymentsManagementDTO.getPerson());
	    request.setAttribute("entriesToSelect", resultingEntries);

	    return mapping.findForward("paymentConfirmed");

	} catch (DomainExceptionWithLabelFormatter ex) {
	    addActionMessage(request, ex.getKey(), solveLabelFormatterArgs(request, ex
		    .getLabelFormatterArgs()));
	    request.setAttribute("paymentsManagementDTO", paymentsManagementDTO);
	    return mapping.findForward("showEvents");
	} catch (DomainException ex) {

	    addActionMessage(request, ex.getKey(), ex.getArgs());
	    request.setAttribute("paymentsManagementDTO", paymentsManagementDTO);
	    return mapping.findForward("showEvents");
	}

    }

    protected Person getPerson(HttpServletRequest request) {
	return (Person) rootDomainObject.readPartyByOID(getIntegerFromRequest(request, "personId"));
    }

    public ActionForward preparePrintGuide(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {
	return internalPreparePrintGuide(mapping, request, "showGuide", "showEvents");
    }

    public ActionForward preparePrintGuideWithInstallments(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {
	return internalPreparePrintGuide(mapping, request, "showGuide", "showEventsWithInstallments");
    }

    private ActionForward internalPreparePrintGuide(final ActionMapping mapping,
	    final HttpServletRequest request, final String successForward, final String errorForward) {

	final PaymentsManagementDTO managementDTO = (PaymentsManagementDTO) RenderUtils.getViewState(
		"paymentsManagementDTO").getMetaObject().getObject();
	request.setAttribute("paymentsManagementDTO", managementDTO);

	if (managementDTO.getSelectedEntries().isEmpty()) {
	    addActionMessage(request,
		    "error.administrativeOffice.payments.guide.entries.selection.is.required");
	    return mapping.findForward(errorForward);
	} else {
	    return mapping.findForward(successForward);
	}
    }

    public ActionForward printGuide(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	PaymentsManagementDTO managementDTO = (PaymentsManagementDTO) RenderUtils.getViewState(
		"paymentsManagementDTO").getMetaObject().getObject();
	request.setAttribute("paymentsManagementDTO", managementDTO);
	request.setAttribute("currentUnit", getCurrentUnit(request));
	return mapping.findForward("printGuide");
    }

    public ActionForward prepareShowReceipt(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	final Person person = getPerson(request);
	final Receipt receipt = getReceipt(request);

	if (receipt == null) {
	    addActionMessage(request, "error.administrativeOffice.payments.receipt.not.found");
	    request.setAttribute("person", person);
	    return mapping.findForward("showReceipts");
	}
	if (!person.getReceiptsSet().contains(receipt)) {
	    addActionMessage(request,
		    "error.administrativeOffice.payments.person.doesnot.contain.receipt");
	    request.setAttribute("person", person);
	    return mapping.findForward("showReceipts");
	}

	request.setAttribute("receipt", receipt);
	return mapping.findForward("showReceipt");
    }

    protected Receipt getReceipt(final HttpServletRequest request) {
	return rootDomainObject.readReceiptByOID(getIntegerFromRequest(request, "receiptID"));
    }

    public ActionForward printReceipt(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) throws FenixFilterException,
	    FenixServiceException {

	final Receipt receipt = (Receipt) RenderUtils.getViewState("receipt").getMetaObject()
		.getObject();
	final SortedSet<Entry> sortedEntries = new TreeSet<Entry>(
		Entry.COMPARATOR_BY_MOST_RECENT_WHEN_REGISTERED);
	sortedEntries.addAll(receipt.getEntries());

	request.setAttribute("receipt", receipt);
	request.setAttribute("sortedEntries", sortedEntries);
	request.setAttribute("currentUnit", getCurrentUnit(request));

	try {

	    ServiceUtils.executeService(getUserView(request), "CreateReceiptVersion", new Object[] {
		    receipt, getUserView(request).getPerson().getEmployee() });

	    return mapping.findForward("printReceipt");

	} catch (InvalidArgumentsServiceException e) {
	    addActionMessage(request, e.getMessage());

	    return prepareShowReceipt(mapping, actionForm, request, response);
	}

    }

    public ActionForward prepareCancelReceipt(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {

	final Receipt receipt = getReceipt(request);
	request.setAttribute("receipt", receipt);
	request.setAttribute("person", receipt.getPerson());

	return mapping.findForward("confirmCancelReceipt");
    }

    protected Receipt getReceiptFromViewState(String viewStateName) {
	final Receipt receipt = (Receipt) RenderUtils.getViewState(viewStateName).getMetaObject()
		.getObject();
	return receipt;
    }

    public ActionForward cancelReceipt(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) throws FenixFilterException,
	    FenixServiceException {

	final Receipt receipt = getReceiptFromViewState("receiptToCancel");
	try {
	    executeService(request, "CancelReceipt", new Object[] {
		    getUserView(request).getPerson().getEmployee(), receipt });

	} catch (DomainException ex) {
	    addActionMessage(request, ex.getKey(), ex.getArgs());
	    request.setAttribute("receipt", receipt);
	    request.setAttribute("person", receipt.getPerson());

	    return mapping.findForward("confirmCancelReceipt");
	}

	request.setAttribute("personId", receipt.getPerson().getIdInternal());

	return showReceipts(mapping, form, request, response);
    }

    protected Unit getCurrentUnit(HttpServletRequest request) {
	return getUserView(request).getPerson().getEmployee().getCurrentWorkingPlace();
    }

    public ActionForward prepareShowPaymentsWithoutReceiptInvalid(ActionMapping mapping,
	    ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) {

	request.setAttribute("createReceiptBean", RenderUtils.getViewState("createReceiptBean")
		.getMetaObject().getObject());
	return mapping.findForward("showPaymentsWithoutReceipt");
    }

    public ActionForward preparePaymentInvalid(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	request.setAttribute("paymentsManagementDTO", RenderUtils.getViewState(
		"paymentsManagementDTO-edit").getMetaObject().getObject());
	return mapping.findForward("preparePayment");
    }

    public ActionForward prepareShowEventsInvalid(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {

	request.setAttribute("paymentsManagementDTO", RenderUtils.getViewState("paymentsManagementDTO")
		.getMetaObject().getObject());

	return mapping.findForward("showEvents");
    }
    
    public ActionForward prepareShowEventsWithInstallmentsInvalid(ActionMapping mapping,
	    ActionForm form, HttpServletRequest request, HttpServletResponse response) {

	request.setAttribute("paymentsManagementDTO", RenderUtils.getViewState("paymentsManagementDTO")
		.getMetaObject().getObject());

	return mapping.findForward("showEventsWithInstallments");
    }

    public ActionForward backToShowOperations(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {
	setContextInformation(request);
	return findMainForward(mapping);
    }

    protected void setContextInformation(HttpServletRequest request) {
	request.setAttribute("person", getPerson(request));
    }

    protected ActionForward findMainForward(final ActionMapping mapping) {
	return mapping.findForward("showOperations");
    }

    abstract protected AdministrativeOffice getAdministrativeOffice(final HttpServletRequest request);

}

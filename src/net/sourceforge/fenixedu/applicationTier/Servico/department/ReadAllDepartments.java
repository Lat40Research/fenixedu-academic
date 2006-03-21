/*
 * 
 *  
 */
package net.sourceforge.fenixedu.applicationTier.Servico.department;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Service;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoDepartment;
import net.sourceforge.fenixedu.domain.Department;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;

public class ReadAllDepartments extends Service {

    public List run() throws FenixServiceException, ExcepcaoPersistencia {
        final List<InfoDepartment> result = new ArrayList<InfoDepartment>();
        for (final Department department : rootDomainObject.getDepartments()) {
            result.add(InfoDepartment.newInfoFromDomain(department));
        }
        return result;
    }
}
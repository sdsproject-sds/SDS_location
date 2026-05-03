package org.sds.sdslocation.repository.accessinterfacerepo;

import org.sds.sdslocation.repository.TblCountry;
import org.springframework.data.repository.CrudRepository;

public interface CountryRepo extends CrudRepository<TblCountry,String> {
}

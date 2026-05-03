package org.sds.sdslocation.repository;

import org.sds.sdslocation.repository.accessinterfacerepo.CountryDivision;
import org.sds.sdslocation.repository.accessinterfacerepo.CountrySubDivisionRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 2:53 PM
 */
@Service
public class DataRepository {
    private final CountryDivision countryDivision;
    private final CountrySubDivisionRepo countrySubDivisionRepo;

    public DataRepository(CountryDivision countryDivision, CountrySubDivisionRepo countrySubDivisionRepo) {
        this.countryDivision = countryDivision;
        this.countrySubDivisionRepo = countrySubDivisionRepo;
    }

    public void saveSubDivision(String geoJson, String divisionCode, String subDivisionName) {
        countrySubDivisionRepo.nativeCreate(divisionCode, subDivisionName, geoJson);
    }

    public void saveDivision(String countryIso2, String divisionCode, String divisionName, String geoString) {
        countryDivision.nativeCreate(countryIso2, divisionCode, divisionName, geoString);
    }

    public List<TblCountrySubDivisions> getSubDivision(Double lon, Double lat) {
        return countrySubDivisionRepo.getSubDivision(lon, lat);
    }

    public List<TblCountryDivisions> getDivision(Double lon, Double lat) {
        return countryDivision.getDivision(lon, lat);
    }

}

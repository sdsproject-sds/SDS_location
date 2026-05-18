package org.sds.sdslocation.repository;

import org.sds.sdslocation.model.CountryDivision;
import org.sds.sdslocation.model.SubDivision;
import org.sds.sdslocation.model.UserLocation;
import org.sds.sdslocation.repository.accessinterfacerepo.CountryDivisionRepos;
import org.sds.sdslocation.repository.accessinterfacerepo.CountrySubDivisionRepo;
import org.sds.sdslocation.repository.accessinterfacerepo.UserLocationRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 2:53 PM
 */
@Service
public class DataRepository {
    private final CountryDivisionRepos countryDivision;
    private final CountrySubDivisionRepo countrySubDivisionRepo;
    private final UserLocationRepo userLocationRepo;

    public DataRepository(CountryDivisionRepos countryDivision, CountrySubDivisionRepo countrySubDivisionRepo, UserLocationRepo userLocationRepo) {
        this.countryDivision = countryDivision;
        this.countrySubDivisionRepo = countrySubDivisionRepo;
        this.userLocationRepo = userLocationRepo;
    }

    public UserLocation saveUserLocation(String deviceId, String userId, double lat, double lon) {
        return userLocationRepo.saveUserLocation(deviceId, userId, lat, lon)
                .userLocation();
    }

    public List<TblDeviceLocation> findNearbyUsers(double lat, double lon) {
        return userLocationRepo.findNearbyUsers(lat, lon, 2000);
    }

    public SubDivision saveSubDivision(String geoJson, String divisionCode, String subDivisionName) {
        return countrySubDivisionRepo.nativeCreate(divisionCode, subDivisionName, geoJson)
                .toSubDivision();
    }

    public CountryDivision saveDivision(String countryIso2, String divisionCode, String divisionName, String geoString) {
        return countryDivision.nativeCreate(countryIso2, divisionCode, divisionName, geoString)
                .toCountryDivision();
    }

    public boolean updateDivision(String divisionCode, String countryIso2, String divisionName,
                                  String geoString, Boolean supported, String updatedBy) {
        int rowsAffected = countryDivision.updateDivision(
                divisionCode,
                countryIso2,
                divisionName,
                geoString,
                supported,
                updatedBy
        );
        return rowsAffected > 0;
    }

    public boolean updateSubDivision(Long id, String divisionCode, String subDivisionName,
                                     String geoString, String updatedBy) {
        int rowsAffected = countrySubDivisionRepo.updateSubDivision(
                id,
                divisionCode,
                subDivisionName,
                geoString,
                updatedBy
        );
        return rowsAffected > 0;
    }

    public List<TblCountrySubDivisions> getSubDivision(Double lon, Double lat) {
        return countrySubDivisionRepo.getSubDivision(lon, lat);
    }

    public List<TblCountryDivisions> getDivision(Double lon, Double lat) {
        return countryDivision.getDivision(lon, lat);
    }

    public TblCountryDivisions getDivisionByDivisionCode(String divisionCode) {
        return countryDivision.findByDivisionCode(divisionCode)
                .orElse(null);
    }

    public TblCountryDivisions getDivisionById(String id) {
        return countryDivision.findByDivisionId(id)
                .orElse(null);
    }

    public SubDivision getSubDivisionById(Long id) {
        return countrySubDivisionRepo.findBySubDivisionId(id)
                .map(TblCountrySubDivisions::toSubDivision)
                .orElse(null);
    }

    public boolean deleteSubDivision(Long id, String deletedBy) {
        int rowsAffected = countrySubDivisionRepo.softDeleteSubDivision(id, deletedBy);
        return rowsAffected > 0;
    }

    public boolean hardDeleteSubDivision(Long id) {
        int rowsAffected = countrySubDivisionRepo.hardDeleteSubDivision(id);
        return rowsAffected > 0;
    }

    public boolean subDivisionExists(Long id) {
        return countrySubDivisionRepo.existsActiveSubDivision(id);
    }

    public boolean deleteDivision(String divisionCode, String deletedBy) {
        int rowsAffected = countryDivision.softDeleteDivision(divisionCode, deletedBy);
        return rowsAffected > 0;
    }

    public boolean hardDeleteDivision(String divisionCode) {
        int rowsAffected = countryDivision.hardDeleteDivision(divisionCode);
        return rowsAffected > 0;
    }

    public boolean divisionExists(String divisionCode) {
        return countryDivision.existsActiveDivision(divisionCode);
    }

    public long countActiveSubDivisions(String divisionCode) {
        return countryDivision.countActiveSubDivisions(divisionCode);
    }

    public boolean isCountryDivisionAvailable(String divisionCode) {
        return countryDivision.findByDivisionCode(divisionCode).isPresent();
    }

}

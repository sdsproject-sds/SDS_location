package org.sds.sdslocation.service.grpc;


import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.sds.sdslocation.grpc.CoordinatesRequest;
import org.sds.sdslocation.grpc.DivisionResponse;
import org.sds.sdslocation.grpc.LocationServiceGrpc;
import org.sds.sdslocation.grpc.SubDivisionResponse;
import org.sds.sdslocation.model.CountryDivisionLookupResponse;
import org.sds.sdslocation.model.PolygonGeometry;
import org.sds.sdslocation.model.SubDivisionLookupResponse;
import org.sds.sdslocation.service.LocationService;
import org.springframework.stereotype.Service;

/**
 * @author Joseph.Kibe. Created On 01 Jun 2026 23:03
 */

@Service
@AllArgsConstructor
public class LocationServiceGrpcImpl extends LocationServiceGrpc.LocationServiceImplBase {

    private final LocationService locationService;
    private final SubDivisionResponseMapper mapper;

    @Override
    public void getSubDivision(CoordinatesRequest request, StreamObserver<SubDivisionResponse> responseObserver) {

        var subDivision = locationService.getSubDivision(request.getLongitude(), request.getLatitude());

        var response = mapper.toProto(subDivision);

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void getDivision(CoordinatesRequest request, StreamObserver<DivisionResponse> responseObserver) {

        var division = locationService.getDivision(request.getLongitude(), request.getLatitude());

        var response = mapper.toProto(division);

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }


    @Mapper(componentModel = "spring")
    public interface SubDivisionResponseMapper {

        @Mapping(source = "subDivision.polygonGeometry", target = "subDivision.geometry")
        @Mapping(target = "mergeFrom", ignore = true)
        @Mapping(target = "clearField", ignore = true)
        @Mapping(target = "clearOneof", ignore = true)
        @Mapping(target = "unknownFields", ignore = true)
        @Mapping(target = "mergeUnknownFields", ignore = true)
        @Mapping(target = "mergeSubDivision", ignore = true)
        @Mapping(target = "allFields", ignore = true)
        SubDivisionResponse toProto(SubDivisionLookupResponse modelResponse);

        @Mapping(source = "countryDivision.polygonGeometry", target = "division.geometry")
        @Mapping(target = "mergeFrom", ignore = true)
        @Mapping(target = "clearField", ignore = true)
        @Mapping(target = "clearOneof", ignore = true)
        @Mapping(target = "unknownFields", ignore = true)
        @Mapping(target = "mergeUnknownFields", ignore = true)
        @Mapping(target = "mergeDivision", ignore = true)
        @Mapping(target = "allFields", ignore = true)
        DivisionResponse toProto(CountryDivisionLookupResponse modelResponse);

        // Direct PolygonGeometry mapping
        default PolygonGeometry map(PolygonGeometry value) {
            return value;
        }

        // If you need String conversion, create a separate method
        @Named("polygonToString")
        default String polygonGeometryToString(PolygonGeometry polygonGeometry) {
            return polygonGeometry != null ? polygonGeometry.toString() : null;
        }

    }

}

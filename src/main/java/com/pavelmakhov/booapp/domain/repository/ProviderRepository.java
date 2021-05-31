package com.pavelmakhov.booapp.domain.repository;

import com.pavelmakhov.booapp.domain.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    List<Provider> findAll();

    Boolean existsByFirstNameAndLastName(String firstName, String lastName);

    List<Provider> findAllByOccupationIgnoreCase(String occupation);

    /**
     * @deprecated used for tests to force id on the created object
     */
    @Deprecated
    @Modifying
    @Transactional(readOnly = true)
    @Query(value = "insert into PROVIDER(ID, FIRST_NAME, LAST_NAME, OCCUPATION, CLINIC_ID) " +
            "values (:id, :firstName, :lastName, :occupation, :clinicId);", nativeQuery = true)
    void insertProviderForceId(@Param("id") long id, @Param("firstName") String firstName,
                               @Param("lastName") String lastName, @Param("occupation") String occupation,
                               @Param("clinicId") Long clinicId);
}

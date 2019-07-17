package com.f1soft.profileservice.utility;

import com.f1soft.profileservice.requestDTO.ProfileDTO;
import org.springframework.util.ObjectUtils;

import java.util.function.Function;

/**
 * @author smriti on 7/9/19
 */
public class QueryCreator {

    public static Function<ProfileDTO, String> createQueryToSearchProfile = (profileDTO -> {
        String query = "";

        query += " SELECT" +
                " p.id," +                          //[0]
                " p.name," +                        //[1]
                " p.status," +                      //[2]
                " p.department_id," +               //[3]
                " p.sub_department_id" +            //[4]
                " FROM" +
                " profile p" +
                " WHERE p.id!=0";

        if (!ObjectUtils.isEmpty(profileDTO.getName()))
            query += " AND p.name='" + profileDTO.getName() + "'";

        if (!ObjectUtils.isEmpty(profileDTO.getDepartmentId()))
            query += " AND p.department_id=" + profileDTO.getDepartmentId();

        if (!ObjectUtils.isEmpty(profileDTO.getSubDepartmentId()))
            query += " AND p.sub_department_id=" + profileDTO.getDepartmentId();

        query += " ORDER BY p.id DESC";

        return query;
    });

    public static Function<Long, String> createQueryToFetchAllProfileDetails = (id) -> {
        return " SELECT" +
                " GROUP_CONCAT((CONCAT(pm.id, '-', pm.role_id,'-',pm.user_menu_id)) SEPARATOR ',')" +
                " AS profile_menu_details," +                                                   //[0]
                " p.description" +                                                              //[1]
                " FROM profile p" +
                " LEFT JOIN profile_menu pm ON p.id = pm.profile_id" +
                " WHERE" +
                " p.id = " + id +
                " AND" +
                " pm.status = 'Y'" +
                " GROUP BY p.id";
    };

}

package au.michalwojcik.messaging.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author michal-wojcik
 */
public interface Mapper {

    ObjectMapper getMapper();
}

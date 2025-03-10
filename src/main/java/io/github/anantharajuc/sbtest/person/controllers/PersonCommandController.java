package io.github.anantharajuc.sbtest.person.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.anantharajuc.sbtest.api.ResourcePaths;
import io.github.anantharajuc.sbtest.api.rate_limiting.APIutil;
import io.github.anantharajuc.sbtest.person.model.Person;
import io.github.anantharajuc.sbtest.person.services.PersonCommandServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Person Command Controller
 *
 * @author <a href="mailto:arcswdev@gmail.com">Anantha Raju C</a>
 *
 */
@RestController
@RequestMapping(value=ResourcePaths.Person.V1.ROOT)
@CacheConfig(cacheNames={"person"})
@Api(value="PersonCommands", tags="Person Commands")
public class PersonCommandController 
{
	@Value("${api.version}")
	private String apiVersion;
	
	@Value("${release.version}")
	private String releaseVersion;
	
	@Autowired
	private PersonCommandServiceImpl personCommandServiceImpl;
	
	/**
	 * Method that creates a person in the database.
	 * 
	 * @author <a href="mailto:arcswdev@gmail.com">Anantha Raju C</a>
	 * 
	 * @param person - person object in JSON format
	 * 
	 * @return ResponseEntity with a <code>Person</code> object and the HTTP status
	 * 
	 * HTTP Status:
	 * 
	 * 201 - Created: Everything worked as expected.
	 */
	@PostMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(httpMethod="POST", value = "Add Person", notes = "Add a new Person to the datastore",response=Person.class)
	@PreAuthorize("hasAnyRole('ADMIN','PERSON') and hasAuthority('PERSON_CREATE')")
	public ResponseEntity<Person> createPerson(@RequestHeader(defaultValue="${api.version}") String apiVersion,
                                               @RequestHeader(value=APIutil.HEADER_API_KEY, defaultValue="${api.key}") String apiKey,
			                                   @Valid @RequestBody Person person)
	{
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();		

		headers.add(APIutil.HEADER_PERSON_API_VERSION, apiVersion);
		headers.add(APIutil.HEADER_API_KEY, apiKey);

		return new ResponseEntity<>(personCommandServiceImpl.createPerson(person), headers, HttpStatus.CREATED);
	}

	/**
	 * Method that updates an existing person in the database.
	 * 
	 * @author <a href="mailto:arcswdev@gmail.com">Anantha Raju C</a>
	 * 
	 * @param personId - the id of the person
	 * @param personDetails - updated <code>Person</code> object
	 * 
	 * @return ResponseEntity with the updated <code>Person</code> object and the HTTP status
	 * 
	 * HTTP Status:
	 * 
	 * 200 - OK: Everything worked as expected.
	 */
	@CacheEvict(allEntries=true)
	@PutMapping(value=ResourcePaths.ID, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(httpMethod="PUT", value = "UPDATE Person", notes = "Update an existing Person in the datastore",response=Person.class)
	@PreAuthorize("hasAnyRole('ADMIN','PERSON') and hasAuthority('PERSON_UPDATE')")
	public ResponseEntity<Person> updatePerson(@RequestHeader(defaultValue="${api.version}") String apiVersion,
                                               @RequestHeader(value=APIutil.HEADER_API_KEY, defaultValue="${api.key}") String apiKey,
			                                   @PathVariable(value = "id") Long personId,@Valid @RequestBody Person personDetails)
	{
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();		

		headers.add(APIutil.HEADER_PERSON_API_VERSION, apiVersion);
		headers.add(APIutil.HEADER_API_KEY, apiKey);
		
		return new ResponseEntity<>(personCommandServiceImpl.updatePerson(personId, personDetails),HttpStatus.OK);
	}
	
	/**
	 * Method that deletes an existing person in the database.
	 * 
	 * @author <a href="mailto:arcswdev@gmail.com">Anantha Raju C</a>
	 * 
	 * @param personId - the id of the person
	 * 
	 * @return ResponseEntity with a Response and the HTTP status
	 * 
	 * HTTP Status:
	 * 
	 * 204 - OK: Everything worked as expected.
	 * 404 - Not Found: The requested resource doesn't exist.
	 * 405 - Method Not Allowed: Resource (Id) to be deleted not supplied
	 */
	@CacheEvict(allEntries=true)
	@DeleteMapping(value=ResourcePaths.ID)
	@ApiOperation(httpMethod="DELETE", value = "DELETE an existing Person", notes = "Delete an existing Person from the datastore")
	@PreAuthorize("hasAnyRole('ADMIN','PERSON') and hasAuthority('PERSON_DELETE')")
	public ResponseEntity<?> deletePerson(@RequestHeader(defaultValue="${api.version}") String apiVersion,
                                          @RequestHeader(value=APIutil.HEADER_API_KEY, defaultValue="${api.key}") String apiKey,
			                              @PathVariable(value = "id") Long personId) 
	{
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();	
		
		headers.add(APIutil.HEADER_PERSON_API_VERSION, apiVersion);
		headers.add(APIutil.HEADER_API_KEY, apiKey);
		
		return new ResponseEntity<>(personCommandServiceImpl.deletePerson(personId), headers, HttpStatus.NO_CONTENT);
	}
}

package com.villadev.rest.user;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/users")
public class UserResource {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	MessageSource msg;
	
	@GetMapping("/hello")
	public String hello(/*@RequestHeader(name="Accept-Language",required=false) Locale locale*/) {
		return msg.getMessage("good.morning.message",null, LocaleContextHolder.getLocale());
	}

	@GetMapping
	public List<User> retrieveAllUsers() {
		return userDAO.findAll();
	}

	@GetMapping("/{id}")
	public Resource<User> retrieveUser(@PathVariable int id) {
		User user = userDAO.findOne(id);

		validateUser(id, user);

		Resource<User> resource = new Resource<User>(user);

		ControllerLinkBuilder linkTo = ControllerLinkBuilder
				.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());

		resource.add(linkTo.withRel("all-users"));

		return resource;
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable int id) {
		User user = userDAO.deleteById(id);
		validateUser(id, user);
	}

	@PostMapping
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser = userDAO.save(user);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId())
				.toUri();

		return ResponseEntity.created(location).build();
	}

	private void validateUser(int id, User user) {
		if (user == null)
			throw new UserNotFoundException("User with id: "+id + " does not exist");
	}
}

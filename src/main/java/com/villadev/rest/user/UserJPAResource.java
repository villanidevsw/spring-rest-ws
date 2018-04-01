package com.villadev.rest.user;


import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.villadev.rest.post.Post;
import com.villadev.rest.post.PostRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/jpa/users")
@Api(description="Api designed to users operations")
public class UserJPAResource {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@ApiOperation(value="returns all users")
	@GetMapping
	public List<User> retrieveAllUsers() {
		return userRepository.findAll();
	}
	
	@ApiOperation(value="returns an user by its id")
	@GetMapping("/{id}")
	public Resource<User> retrieveUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);

		validate(id, user);

		Resource<User> resource = new Resource<User>(user.get());

		ControllerLinkBuilder linkTo = ControllerLinkBuilder.
				linkTo(ControllerLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());

		resource.add(linkTo.withRel("all-users"));
		return resource;
	}

	private void validate(int id, Optional<User> user) {
		if (!user.isPresent())
			throw new UserNotFoundException("User with id: "+id + " does not exist");
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);
	}

	@PostMapping
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId())
				.toUri();

		return ResponseEntity.created(location).build();

	}
	
	@GetMapping("/{id}/posts")
	public List<Post> retrieveAllPostsFromUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		validate(id, user);		
		return user.get().getPosts();
	}


	@PostMapping("/{id}/posts")
	public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post) {	
		Optional<User> user = userRepository.findById(id);
		validate(id, user);
		post.setUser(user.get());
		Post savedPost = postRepository.save(post);
		/*Resource<Post> resource = new Resource<Post>(postRepository.save(post));

		ControllerLinkBuilder linkTo = ControllerLinkBuilder.
				linkTo(ControllerLinkBuilder.methodOn(this.getClass()).retrieveAllPostsFromUser(user.get().getId()));

		resource.add(linkTo.withRel("all-posts-from-user"));*/
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedPost.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();

	}

}


// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});


// This is called before an item is saved.
// We'd want to check that the number of likes is 0 here for example (but only when an item is created, not when it's updated).
// Parse.Cloud.beforeSave("Item", function(request, response) {
//	
// });


// This function allows users to like an item, even though they don't own it.
// Need to make sure no one can like an object multiple times too.
// Create a "likes" relation for each user that says what objects they've liked.
Parse.Cloud.define("incrementLikesForItem", function(request, response) {
	Parse.Cloud.useMasterKey();
	var item = request.params.object();
	item.increment("likes");
	item.save(null, {
		success: function(item) {
			// the save was successful.
			response.success("Item liked successfully");
		},
		error: function(item, error) {
			response.error("Error saving item");
		}
	});
});
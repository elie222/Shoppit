
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
// Parse.Cloud.define("incrementLikesForItem", function(request, response) {
//  Parse.Cloud.useMasterKey();
//  var item = request.params.object();
//  item.increment("likes");
//  item.save(null, {
//      success: function(item) {
//          // the save was successful.
//          response.success("Item liked successfully");
//      },
//      error: function(item, error) {
//          response.error("Error saving item");
//      }
//  });
// });

Parse.Cloud.define("likeItem", function (request, response) {
    if (!request.user) {
        response.error("Can't like an item when not logged in.");
        return;
    }

    Parse.Cloud.useMasterKey();
    var itemId = request.params.itemId;
    
    var query = new Parse.Query("Item");
    query.get(itemId, {
        success: function(item) {
            var relation = item.relation("likes");

            if (request.params.like) {
                relation.add(request.user);
                item.increment("likesCount", 1);
            } else {
                relation.remove(request.user);
                item.increment("likesCount", -1);
            }

            item.save(null, {
                success: function(item) {
                    // the save was successful.
                    console.log(item);
                    response.success("Item liked successfully");
                },
                error: function(item, error) {
                    response.error("Error1 saving item " + error.message);
                }
            });
        },
        error: function(item, error) {
            response.error("Error2 saving item " + error.message);
        }
    });

});

// this is in array format. changed to string instead
// Parse.Cloud.beforeSave("Item", function (request, response) {

//     var name = request.object.get("name");
//     var category = request.object.get("mainCategory");
//     var keywords = request.object.get("keywords");

//     console.log(name);
//     console.log(category);
//     console.log(keywords);

//     var searchArray = [];

//     searchArray.push(name.toLowerCase());
//     searchArray.push(category.toLowerCase());

//     for (var index in keywords) {
//         searchArray.push(keywords[index].toLowerCase());
//     }

//     console.log(searchArray);

//     request.object.set("searchArray", searchArray);

//     response.success();
// });

Parse.Cloud.beforeSave("Item", function (request, response) {

    if (!request.object.existed()) {
        // search terms to find item
        var name = request.object.get("name");
        var category = request.object.get("mainCategory");
        var keywords = request.object.get("keywords");

        searchString = "";
        searchString += (name.toLowerCase() + " ");
        searchString += (category.toLowerCase() + " ");

        for (var index in keywords) {
            searchString += (keywords[index].toLowerCase() + " ");
        }

        console.log(searchString);

        request.object.set("searchString", searchString);
    }

    response.success();
});
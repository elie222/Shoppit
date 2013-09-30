
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

// this allows a user to like an item while not changing the item itself, thereby maintaining object security.
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

// this is so that we can search for items in the database easily
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
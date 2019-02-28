I built this application to get reacquainted with Java, experiment with some Java features (I hadn&#39;t used anything beyond Java 7 previously), and start learning Spring Boot. It is inspired by a very scaled-down version of Twitter.

**Basic functionality**

Users can write a tweet, follow other users, comment on tweets written by users they follow, and read all comments on the tweets of users they&#39;re following. Anyone, even without an account, can view tweets. Users must have an account to publish a tweet or comment.

**To run the application**

The application is configured to run on localhost:8080/liltwitter. There is only an API, no UI. The application uses an in-memory database that does not persist between invocations of the application, though it is seeded at launch with a handful of users, tweets, following relationships between users, and comments.

You can clone the repo and either import the project into an IDE and run it there, or create an executable JAR file by navigating to the project&#39;s root directory and running &#39;mvn package -Dmaven.test.skip=true&#39; there, then running the command &#39;java -jar target/messageservice-0.0.1-SNAPSHOT.jar&#39;. You can interact with the API as a client with Postman, CURL, or whatever other tool you like.

**Available operations**

GET to &quot;/user&quot; endpoint returns a list of all User objects.

GET to &quot;/user/{userName}/tweets&quot; endpoint returns a list of all Tweet objects written by the specified user.

GET to &quot;/user/{userName}/followedby&quot; endpoint returns a list of User objects whose users are following the specified user.

GET to &quot;/user/{userName}/following&quot; endpoint returns a list of User objects whose users the specified user is following.

GET to &quot;/user/{userName}&quot; endpoint returns a list of all Tweet objects written by the specified user.

POST to &quot;/tweet/comments&quot; expects a JSON object containing the fields requesterName and tweetId. If the user follows the author of the specified tweet, it returns a list of all Comment objects associated with that tweet.

POST to &quot;/allContentFromUser&quot; expects a JSON object containing the fields requesterName and posterUserName. If the requester follows the specified user, it returns a nested list of the tweets associated with that user, as well as all comments on those tweets. If the requester either doesn&#39;t follow the user, or doesn&#39;t exist, it returns just the tweets without comments.

POST to &quot;new/user&quot; expects a JSON object that must contain the username field and can optionally include a name field and/or a location field. If successful, it returns the User object that was created.

POST to &quot;/new/follow&quot; expects a JSON object that contains the fields followerName and followedName. If successful, it returns the UserRelationship object that was created.

POST to &quot;/new/comment&quot; expects a JSON object that contains the fields tweetId, message, and commenterUserName. If the commenter follows the author of the referenced tweet, it saves the comment and returns a Comment object timestamped with the time the request was received.

POST to &quot;/new/tweet&quot; expects a JSON object that contains the fields userName and message. If successful, it saves the tweet and returns a Tweet object timestamped with the time the request was received.

GET to &quot;/user/{userName}/allContentFromAllFollowedUsers&quot; endpoint returns a nested list of all users the requester follows, all of their respective tweets, and all the comments on those tweets.

POST to &quot;/new/unfollow&quot; expects a JSON object that contains the fields followerName and followedName. If the requester is following the other user, the relationship is deleted.

**Next steps if I were to keep working on this**

Testing

I&#39;d like to include programmatic testing. Since this was my first time working with Spring Boot, it took some time to figure out the project structure and make sure the pieces were communicating with each other; I wanted to dive right in before writing tests. Once the API was responding to requests, I did start writing tests. I wrote unit tests for methods in the service and briefly tried writing tests mocking the API. I tried to figure out how to inject my dependencies to get the tests to run successfully, and I also tried using Mockito for the API. My previous experience was all with JUnit, though, and I didn&#39;t want to spend too much time on testing rather than on getting more functionality into the application. I used the tests I wrote as a guide for testing the application as if I were a real user, and I do wish I&#39;d gotten the tests working—it was frustrating to make a change and then have to manually run multiple tests in Postman. I&#39;d like to return to the project when I have more time and figure out the dependency issues and get more familiar with Mockito syntax. I&#39;d also like to consider additional levels of testing and the best tools for them, such as integration testing and load testing.

Make sure I&#39;m following best practices

I wanted to get a working prototype finished quickly, so there are cases where I chose or changed a particular Spring annotation just because it seemed to work. For example, I changed to GenerationType.IDENTITY because StackOverflow told me to without really understanding why. I also defaulted to HttpStatus.BAD\_REQUEST for any failed request instead of sending different response codes for, say, improperly formatted requests vs. requests that were illegal because of what data was in the database. I suspect my file structure and separation of concerns also isn&#39;t standard. Finally, I&#39;m not happy with my API endpoints. The paths I defined were not very consistent, and I used POST requests to fetch information when I needed to include both a target and the requester&#39;s ID. I did that because I figured user info would be stored in a session in the future and didn&#39;t like seeing the requester&#39;s name in the classpath and the target, but I&#39;d ultimately prefer just the target in the path and user info in the session.

Create a UI

Because I&#39;m used to interacting with real Twitter via a UI, it would have been nice to get a visual representation of everything happening under the hood. That said, while I have experience with the MVC pattern, my experience with front-end work is limited and often done grudgingly.

Logging, Monitoring, Metrics

Given the small scale of the application, all of these seemed like overkill, but if I were to continue building this out as if it were an actual production application with multiple concurrent users and persistent data, I&#39;d eventually want to consider these features.

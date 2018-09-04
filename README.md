# YouDMC
Project YouDMC First Round Specifications

Back-end / Database
	The back-end / database mainly stores three types of data: User, Message, and Page
	A User should contain
	{
		UserID(Primary Key) 	//this is hidden
		User Name				//this is displayed, can be forced to be unique
		User Type				//Admin(can mod users and messages), Modderator(can mod messages only), User(can post and view), Banned User(Can only view)
		Email					//registered email, obviously unique
		Salt					//randomly generated
		Hashed Password
	}	
	
	A message should contain
	{
		MessageID(Primary Key) 	//this is hidden
		User ID					//the user that posted this message
		Reply to				//The message this message is in respond to, if this message is at "root level", then the reply to should be the page value
		Body					//what the message includes
	}
	
	
	A page should contain
	{
		PageID(Primary Key)		//this is hidden, it CAN be the video ID		
	}

	Queries/Methods should include
	{
		getMessages(PageID);	//list all the Messages related to a page
		getMessages(MessageID);	//list all the Messages responding to a given message
		getUserPosts(UserID);	//returns list of posts posted by a User
	}
	
Front-end / App and Web
	Both the App and the Web should provide a similar experience, the main features should include
	Home Page
		An explanation of our service, vision, and rules.
	Registration System
		For new users to register an account.
	Profile Management
		For registered users to 
	Commenting System / Pages
		The core of our service. Getting the messages to be displayed correctly and allowing the user to add new comments.
	
Future ideas
	It is important to focus the first round on only the essentials and not get side-tracked, but good ideas should not be forgotten. Therefore, this section should include features that can be implemented in future builds

	"Sign-in Via" System
	Ban timer for users
	Spam filter
	Report System

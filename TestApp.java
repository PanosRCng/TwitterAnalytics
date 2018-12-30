import Apps.FetchDataOverTime;
import Apps.GeneralFunctions;
import Apps.Replies.RepliesApp;
import Apps.Retweeters.RetweetersApp;
import Apps.Retweets.RetweetsApp;

import static java.lang.Boolean.TRUE;

public class TestApp
{

	public static void main(String[] args)
	{

		//RepliesApp repliesApp = new RepliesApp(Boolean.TRUE);

		//RetweetsApp retweetsApp = new RetweetsApp(Boolean.TRUE, Boolean.TRUE);

		//RetweetersApp retweetersApp = new RetweetersApp(Boolean.TRUE);

		//GeneralFunctions generalFunctions = new GeneralFunctions();
		//generalFunctions.storeCentralityResult("alpha");

		FetchDataOverTime fetchDataOverTime = new FetchDataOverTime();

		System.out.println("all ok");
	}

}

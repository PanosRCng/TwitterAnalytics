package Apps.Retweeters;

import TwitterAnalytics.Services.HubRetweeterService;

public class RetweetersApp {

    private static final HubRetweeterService HubRetweeterServiceService = new HubRetweeterService();

    public RetweetersApp() {

        System.out.println(HubRetweeterServiceService.getAll());
    }
}

package org.dailycodebuffer.codebufferspringbootmongodb.baeldung.callback_test1;

public class ActionHandler {

    private ServiceExample serviceExample;

    public ActionHandler(ServiceExample serviceExample) {
        this.serviceExample=serviceExample;
    }
    public void doAction(){
        serviceExample.doAction("ActionHandler Request",new CallbackImpl<Response>());
    }
}

package ca.sozoservers.dev;

import ca.sozoservers.dev.database.models.ExampleModel;

public class Main {
    public static void main(String[] args) {
        ExampleModel model = new ExampleModel();
        model.activeLFGs = "asda";
        model.lfgChannel = 2;
        model.server = 1l;
        model.template = 3l;
        System.out.println(model.toJSON());
    }
}

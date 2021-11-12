package ca.sozoservers.dev.database.models;

import java.lang.reflect.Field;

import ca.sozoservers.dev.database.models.DatabaseModel.*;

@Table("server")
public class ServerModel extends DatabaseModel {

    @Constraints("PRIMARY KEY")
    @DataType("INTERGER")
    public long server = 0;

    @DataType("INTERGER")
    public long template = 0;;

    @DataType("INTERGER")
    public long lfgChannel = 0;

    @DataType("TEXT")
    public String activeLFGs = "";
}

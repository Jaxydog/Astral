package dev.jaxydog.utility.register;

import java.lang.reflect.Method;
import dev.jaxydog.Astral;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.util.Identifier;

/** Provides a set of interfaces for registering values at runtime */
public interface Registerable {

    /** Returns the value's *raw* identifier, used for generating the actual identifier */
    public String getRawId();

    /** Returns the value's associated identifier */
    public default Identifier getId() {
        return Astral.getId(this.getRawId());
    }

    /** A value registered on both the client and the server */
    public static interface Main extends Registerable {

        /** Registers the value on both the client and server */
        public void registerMain();

    }

    /** A value registered on the client */
    public static interface Client extends Registerable {

        /** Registers the value on the client */
        public void registerClient();

    }

    /** A value registered on the server */
    public static interface Server extends Registerable {

        /** Registers the value on the server */
        public void registerServer();

    }

    /** A value registered on the datagen client */
    public static interface Datagen extends Registerable {

        /** Registers the value on the datagen client */
        public void registerDatagen(FabricDataGenerator generator);

    }

    /** A value registered in all environments */
    public static interface All extends Main, Client, Server, Datagen {
    }

    /** Used in the registration process to distinguish between different target environments */
    public static enum Env {

        /** The client and the server */
        MAIN(Main.class, "registerMain"),
        /** The client */
        CLIENT(Client.class, "registerClient"),
        /** The server */
        SERVER(Server.class, "registerServer"),
        /** The datagen client */
        DATAGEN(Datagen.class, "registerDatagen");

        /** The environment's corresponding registerable interface */
        private final Class<? extends Registerable> INTERFACE;
        /** The name of the method used to register the value */
        private final String METHOD_NAME;

        private Env(Class<? extends Registerable> iface, String methodName) {
            this.INTERFACE = iface;
            this.METHOD_NAME = methodName;
        }

        /** Returns the environment's corresponding registerable interface */
        public final Class<? extends Registerable> getInterface() {
            return this.INTERFACE;
        }

        /** Returns the environment's registration method */
        public final Method getMethod() throws NoSuchMethodException {
            if (this == Env.DATAGEN) {
                return this.getInterface().getMethod(this.METHOD_NAME, FabricDataGenerator.class);
            } else {
                return this.getInterface().getMethod(this.METHOD_NAME);
            }
        }

        /** Returns whether a field should be skipped based on its annotation */
        public final boolean isSkipped(Skip annotation) {
            switch (this) {
                case MAIN:
                    return annotation.main();
                case CLIENT:
                    return annotation.client();
                case SERVER:
                    return annotation.server();
                case DATAGEN:
                    return annotation.datagen();
                default:
                    return true;
            }
        }

    }

}

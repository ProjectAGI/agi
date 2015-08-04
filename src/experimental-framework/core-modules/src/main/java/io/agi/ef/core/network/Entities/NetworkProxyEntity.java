package io.agi.ef.core.network.entities;

import io.agi.ef.clientapi.model.TStamp;
import io.agi.ef.core.UniversalState;
import io.agi.ef.core.network.ServerConnection;
import io.agi.ef.serverapi.api.ApiResponseMessage;

import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gideon on 3/08/15.
 */
public class NetworkProxyEntity extends AbstractEntity {

    private static final Logger _logger = Logger.getLogger( NetworkProxyEntity.class.getName() );
    ServerConnection _sc = null;

    public NetworkProxyEntity( ServerConnection sc ) {
        super();
        _sc = sc;
    }

    @Override
    protected Logger getLogger() {
        return _logger;
    }

    @Override
    public Response run() {
        return Response.ok().entity( new ApiResponseMessage( ApiResponseMessage.OK, "Entity run." ) ).build();
    }

    @Override
    public Response step() {

        Response response = null;

        if ( _sc.getClientApi() == null ) {
            response = Response.ok().entity( new ApiResponseMessage( ApiResponseMessage.OK, "The server connection is invalid." ) ).build();
        }
        else {
            HashMap<String, List<TStamp>> serverTimeStamps = new HashMap<>();
            List<io.agi.ef.clientapi.model.TStamp> tStamps;

            io.agi.ef.clientapi.api.ControlApi capi = new io.agi.ef.clientapi.api.ControlApi( _sc.getClientApi() );
            try {

                // todo: known bug: doesn't seem to be able to deserialise tStamps, but i want to change their format anyway
                tStamps = capi.controlStepGet();
                serverTimeStamps.put( _sc.basePath(), tStamps );
            }
            catch ( io.agi.ef.clientapi.ApiException e ) {
                e.printStackTrace();
            }
            // todo: this catches a connection refused exception, but should be tidied up
            catch ( Exception e ) {
                e.printStackTrace();
            }
            response = Response.ok().entity( serverTimeStamps ).build();
        }

        return response;
    }

    @Override
    public Response stop() {
        return Response.ok().entity( new ApiResponseMessage( ApiResponseMessage.OK, "Entity stopped." ) ).build();
    }

    @Override
    public UniversalState getState() {
        UniversalState state = null;
        return state;
    }

    @Override
    public void setWorldState( UniversalState state ) {

    }

    @Override
    public void setAgentStates( Collection<UniversalState> agentStates ) {

    }
}

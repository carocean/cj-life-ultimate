package cj.life.ability.oauth2.gateway.client;

import cj.life.ability.oauth2.gateway.ILookupClient;
import cj.life.ability.oauth2.gateway.properties.ClientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultLookupClient implements ILookupClient {
    Map<String,ClientInfo> clients;

    public DefaultLookupClient(List<ClientInfo> clients) {
        this.clients = new HashMap<>();
        for (ClientInfo clientInfo : clients) {
            this.clients.put(clientInfo.getClient_id(), clientInfo);
        }
    }

    @Override
    public ClientInfo lookup(String client_id) {
        if (clients.containsKey(client_id)) {
            return clients.get(client_id);
        }
        return null;
    }
}

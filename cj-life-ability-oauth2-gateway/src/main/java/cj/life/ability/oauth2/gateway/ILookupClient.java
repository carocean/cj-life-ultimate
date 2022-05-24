package cj.life.ability.oauth2.gateway;

import cj.life.ability.oauth2.gateway.properties.ClientInfo;

public interface ILookupClient {
    ClientInfo lookup(String client_id);
}

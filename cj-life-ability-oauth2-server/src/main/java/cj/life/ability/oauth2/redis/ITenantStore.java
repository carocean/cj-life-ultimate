package cj.life.ability.oauth2.redis;

public interface ITenantStore {
    public void storeTenant(String user,String client_id, String tenant_id) ;
}

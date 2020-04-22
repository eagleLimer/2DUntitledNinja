might be a fun movement system for some zomboozles
@Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(pm.get(player) != null) {
            Vector3 playerPos = pm.get(player).position;
            Vector3 enemyPos = pm.get(entity).position;
            BodyComponent body = bodm.get(entity);
            VelocityComponent velocity = vm.get(entity);

            
            if (playerPos.x < enemyPos.x) {
                body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, -velocity.sprintSpeed, ACCELERATION), body.body.getLinearVelocity().y);
            } else {
                body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, velocity.sprintSpeed, ACCELERATION), body.body.getLinearVelocity().y);
            }
        }
    }

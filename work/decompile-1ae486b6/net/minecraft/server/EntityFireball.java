package net.minecraft.server;

public abstract class EntityFireball extends Entity {

    private int e = -1;
    private int f = -1;
    private int g = -1;
    private Block h;
    private boolean au;
    public EntityLiving shooter;
    private int av;
    private int aw;
    public double dirX;
    public double dirY;
    public double dirZ;

    public EntityFireball(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
    }

    protected void i() {}

    public EntityFireball(World world, double d0, double d1, double d2, double d3, double d4, double d5) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
        this.setPosition(d0, d1, d2);
        double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        this.dirX = d3 / d6 * 0.1D;
        this.dirY = d4 / d6 * 0.1D;
        this.dirZ = d5 / d6 * 0.1D;
    }

    public EntityFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(world);
        this.shooter = entityliving;
        this.setSize(1.0F, 1.0F);
        this.setPositionRotation(entityliving.locX, entityliving.locY, entityliving.locZ, entityliving.yaw, entityliving.pitch);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        d0 += this.random.nextGaussian() * 0.4D;
        d1 += this.random.nextGaussian() * 0.4D;
        d2 += this.random.nextGaussian() * 0.4D;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        this.dirX = d0 / d3 * 0.1D;
        this.dirY = d1 / d3 * 0.1D;
        this.dirZ = d2 / d3 * 0.1D;
    }

    public void m() {
        if (!this.world.isClientSide && (this.shooter != null && this.shooter.dead || !this.world.isLoaded(new BlockPosition(this)))) {
            this.die();
        } else {
            super.m();
            if (this.k()) {
                this.setOnFire(1);
            }

            if (this.au) {
                if (this.world.getType(new BlockPosition(this.e, this.f, this.g)).getBlock() == this.h) {
                    ++this.av;
                    if (this.av == 600) {
                        this.die();
                    }

                    return;
                }

                this.au = false;
                this.motX *= (double) (this.random.nextFloat() * 0.2F);
                this.motY *= (double) (this.random.nextFloat() * 0.2F);
                this.motZ *= (double) (this.random.nextFloat() * 0.2F);
                this.av = 0;
                this.aw = 0;
            } else {
                ++this.aw;
            }

            MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, true, this.aw >= 25, this.shooter);

            if (movingobjectposition != null) {
                this.a(movingobjectposition);
            }

            this.locX += this.motX;
            this.locY += this.motY;
            this.locZ += this.motZ;
            ProjectileHelper.a(this, 0.2F);
            float f = this.l();

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;

                    this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX - this.motX * 0.25D, this.locY - this.motY * 0.25D, this.locZ - this.motZ * 0.25D, this.motX, this.motY, this.motZ, new int[0]);
                }

                f = 0.8F;
            }

            this.motX += this.dirX;
            this.motY += this.dirY;
            this.motZ += this.dirZ;
            this.motX *= (double) f;
            this.motY *= (double) f;
            this.motZ *= (double) f;
            this.world.addParticle(this.j(), this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D, new int[0]);
            this.setPosition(this.locX, this.locY, this.locZ);
        }
    }

    protected boolean k() {
        return true;
    }

    protected EnumParticle j() {
        return EnumParticle.SMOKE_NORMAL;
    }

    protected float l() {
        return 0.95F;
    }

    protected abstract void a(MovingObjectPosition movingobjectposition);

    public static void a(DataConverterManager dataconvertermanager, String s) {}

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("xTile", this.e);
        nbttagcompound.setInt("yTile", this.f);
        nbttagcompound.setInt("zTile", this.g);
        MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.b(this.h);

        nbttagcompound.setString("inTile", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setByte("inGround", (byte) (this.au ? 1 : 0));
        nbttagcompound.set("direction", this.a(new double[] { this.motX, this.motY, this.motZ}));
        nbttagcompound.set("power", this.a(new double[] { this.dirX, this.dirY, this.dirZ}));
        nbttagcompound.setInt("life", this.av);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.e = nbttagcompound.getInt("xTile");
        this.f = nbttagcompound.getInt("yTile");
        this.g = nbttagcompound.getInt("zTile");
        if (nbttagcompound.hasKeyOfType("inTile", 8)) {
            this.h = Block.getByName(nbttagcompound.getString("inTile"));
        } else {
            this.h = Block.getById(nbttagcompound.getByte("inTile") & 255);
        }

        this.au = nbttagcompound.getByte("inGround") == 1;
        NBTTagList nbttaglist;

        if (nbttagcompound.hasKeyOfType("power", 9)) {
            nbttaglist = nbttagcompound.getList("power", 6);
            if (nbttaglist.size() == 3) {
                this.dirX = nbttaglist.e(0);
                this.dirY = nbttaglist.e(1);
                this.dirZ = nbttaglist.e(2);
            }
        }

        this.av = nbttagcompound.getInt("life");
        if (nbttagcompound.hasKeyOfType("direction", 9) && nbttagcompound.getList("direction", 6).size() == 3) {
            nbttaglist = nbttagcompound.getList("direction", 6);
            this.motX = nbttaglist.e(0);
            this.motY = nbttaglist.e(1);
            this.motZ = nbttaglist.e(2);
        } else {
            this.die();
        }

    }

    public boolean isInteractable() {
        return true;
    }

    public float aA() {
        return 1.0F;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.ap();
            if (damagesource.getEntity() != null) {
                Vec3D vec3d = damagesource.getEntity().aB();

                if (vec3d != null) {
                    this.motX = vec3d.x;
                    this.motY = vec3d.y;
                    this.motZ = vec3d.z;
                    this.dirX = this.motX * 0.1D;
                    this.dirY = this.motY * 0.1D;
                    this.dirZ = this.motZ * 0.1D;
                }

                if (damagesource.getEntity() instanceof EntityLiving) {
                    this.shooter = (EntityLiving) damagesource.getEntity();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public float e(float f) {
        return 1.0F;
    }
}
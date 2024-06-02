/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.DynSurround.client.fx.particle;

import org.blockartistry.mod.DynSurround.util.Color;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityTextPopOffFX extends EntityFX {

	protected static final float GRAVITY = 0.8F;
	protected static final float SIZE = 3.0F;
	protected static final int LIFESPAN = 12;
	protected static final double BOUNCE_STRENGTH = 1.5F;

	protected Color renderColor;
	protected String text;
	protected boolean shouldOnTop = false;
	protected boolean grow = true;
	protected float scale = 1.0F;

	public EntityTextPopOffFX(final World world, final String text, final Color color, final float scale,
			final double x, final double y, final double z, final double dX, final double dY, final double dZ) {
		super(world, x, y, z, dX, dY, dZ);

		this.text = text;
		this.renderColor = color;
		this.motionX = dX;
		this.motionY = dY;
		this.motionZ = dZ;
		final float dist = MathHelper
				.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX = (this.motionX / dist * 0.12D);
		this.motionY = (this.motionY / dist * 0.12D);
		this.motionZ = (this.motionZ / dist * 0.12D);
		this.particleTextureJitterX = 1.5F;
		this.particleTextureJitterY = 1.5F;
		this.particleGravity = GRAVITY;
		this.particleScale = SIZE;
		this.particleMaxAge = LIFESPAN;
	}

	@Override
	public void renderParticle(final Tessellator p_70539_1_, final float x, final float y, final float z,
			final float dX, final float dY, final float dZ) {
		this.rotationYaw = (-Minecraft.getMinecraft().thePlayer.rotationYaw);
		this.rotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

		final float locX = ((float) (this.prevPosX + (this.posX - this.prevPosX) * x - interpPosX));
		final float locY = ((float) (this.prevPosY + (this.posY - this.prevPosY) * y - interpPosY));
		final float locZ = ((float) (this.prevPosZ + (this.posZ - this.prevPosZ) * z - interpPosZ));

		GL11.glPushMatrix();
		if (this.shouldOnTop) {
			GL11.glDepthFunc(GL11.GL_ALWAYS);
		} else {
			GL11.glDepthFunc(GL11.GL_LEQUAL);
		}
		GL11.glTranslatef(locX, locY, locZ);
		GL11.glRotatef(this.rotationYaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(this.rotationPitch, 1.0F, 0.0F, 0.0F);

		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glScaled(this.particleScale * 0.008D, this.particleScale * 0.008D, this.particleScale * 0.008D);
		GL11.glScaled(this.scale, this.scale, this.scale);

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		fontRenderer.drawStringWithShadow(this.text,
				-MathHelper.floor_float(fontRenderer.getStringWidth(this.text) / 2.0F) + 1,
				-MathHelper.floor_float(fontRenderer.FONT_HEIGHT / 2.0F) + 1, this.renderColor.rgb());

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		GL11.glPopMatrix();
		if (this.grow) {
			this.particleScale *= 1.08F;
			if (this.particleScale > SIZE * 3.0D) {
				this.grow = false;
			}
		} else {
			this.particleScale *= 0.96F;
		}
	}

	@Override
	public int getFXLayer() {
		return 3;
	}
}

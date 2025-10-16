from snake_obj import Snake,Food,Power
from functions import paused
from time import time
import pygame as pg
import os,sys
import random

pg.init()
pg.display.init()


screen = pg.display.set_mode((0,0),pg.FULLSCREEN)
#offset dello screen (fullscreen buggato al cazzo)
x_off = 200
y_off = 100
center = (screen.get_width() // 2, screen.get_height() // 2)

FPS = 20
FPS_backup = 20
clock = pg.time.Clock()
collision_pos = ()

snake_color = (255,255,255)
player_1_sd = [1,0]
player_1 = Snake(screen,center,10,player_1_sd,snake_color); ss = 0

score = pg.font.SysFont('Comic Sans MS',110,1)
Boost = pg.font.SysFont('Comic Sans MS',20,1)
power_1 = pg.font.SysFont('Comic Sans MS',20,1)
power_2 = pg.font.SysFont('Comic Sans MS',20,1)

#food initial generation
food_pos = (random.randint(x_off,screen.get_width()-(x_off+100)), random.randint(y_off,screen.get_height()-(y_off+100)))
f = Food(food_pos,screen)

#powerup setting
random.seed(time())
p_p_t = (random.randint(x_off,screen.get_width()-x_off), random.randint(y_off,screen.get_height()-y_off))
p_p_s = (random.randint(x_off,screen.get_width()-x_off), random.randint(y_off,screen.get_height()-y_off))
tp = Power(screen,p_p_t,100,'teleport',(0,0,255)); time_TP = 0
slw = Power(screen,p_p_s,100,'time slower',(112,0,122)); time_SLW = 0; using_time = 0

#GAME LOOP
if __name__=="__main__":
  while True:
    clock.tick(FPS)
    if (tp.powerup_spawned()):
      time_TP += 1
      if time_TP == tp.dt:
        tp.sp = False
        time_TP = 0
    else:
      if not random.randint(0,300):
        tp.set_position((random.randint(x_off,screen.get_width()-x_off), random.randint(y_off,screen.get_height()-y_off)))
        tp.sp = True
    if (slw.powerup_spawned()):
      time_SLW += 1
      if time_SLW == slw.dt:
        slw.set_position((random.randint(x_off,screen.get_width()-x_off), random.randint(y_off,screen.get_height()-y_off)))
        slw.sp = False
        time_SLW = 0
    else:
      if not random.randint(0,300):
        slw.sp = True
        
    if slw.name in player_1.get_powerups():
      using_time+=1
      if using_time >= slw.at:
        using_time = 0
        FPS = FPS_backup
        player_1.set_powerup('boost',False)
        player_1.inventory.remove(slw.name)
        player_1.active_powers.update({slw.name:False})
    score_srf = score.render(str(ss),1,(0,255,200))
    boost_srf = Boost.render("Spacebar for BOOST",1,(120,120,0))
    try:
      power_1_srf = power_1.render('SHIFT: '+str(player_1.inventory[1]),1,(0,255,200))
      power_2_srf = power_2.render('ALT: '+str(player_1.inventory[2]),1,(0,255,200))
    except IndexError:
      ...
    for event in pg.event.get():
      if event.type == pg.QUIT:
        pg.quit()
        sys.exit()
      ##
      if event.type == pg.KEYDOWN:
        if event.key == pg.K_ESCAPE:
          pg.quit()
          sys.exit()
        if event.key == pg.K_PAUSE:
          paused(True,screen)
        if event.key == pg.K_s and player_1_sd[0] != 0:
          player_1_sd[1] = 1
          player_1_sd[0] = 0
        if event.key == pg.K_w and player_1_sd[0] != 0:
          player_1_sd[1] = -1
          player_1_sd[0] = 0
        if event.key == pg.K_a and player_1_sd[1] != 0:
          player_1_sd[0] = -1
          player_1_sd[1] = 0
        if event.key == pg.K_d and player_1_sd[1] != 0:
          player_1_sd[0] = 1
          player_1_sd[1] = 0
        if event.key == pg.K_SPACE and not 'boost' in player_1.get_powerups():
          FPS_backup = FPS
          FPS += 20
          player_1.set_powerup('boost',True)
        try:
          if event.key == pg.K_LSHIFT and not player_1.inventory[1] in player_1.get_powerups():
            if player_1.inventory[1] == tp.name:
              (x,y) = pg.mouse.get_pos()
              player_1.replace(x,y)
              player_1.inventory.remove(tp.name)
            elif player_1.inventory[1] == slw.name:
              FPS_backup = FPS
              FPS /= 5
              player_1.set_powerup('boost',True)
              player_1.set_powerup(slw.name,True)
          if event.key == pg.K_LALT and not player_1.inventory[2] in player_1.get_powerups():
            if player_1.inventory[2] == tp.name:
              (x,y) = pg.mouse.get_pos()
              player_1.replace(x,y)
              player_1.inventory.remove(tp.name)
            if player_1.inventory[2] == slw.name:
              FPS_backup = FPS
              FPS /= 5
              player_1.set_powerup('boost',True)
              player_1.set_powerup(slw.name,True)
        except IndexError:
          ...
      if event.type == pg.KEYUP:
        if event.key == pg.K_SPACE and 'boost' in player_1.get_powerups():
          FPS = FPS_backup
          player_1.set_powerup('boost', False)
        if event.key == pg.K_LSHIFT and slw.name in player_1.get_powerups():
          FPS = FPS_backup
          player_1.set_powerup('boost',False)
          player_1.set_powerup(slw.name,False)
          player_1.inventory.remove(slw.name)
          using_time = 0
        if event.key == pg.K_LALT and slw.name in player_1.get_powerups():
          FPS = FPS_backup
          player_1.set_powerup('boost',False)
          player_1.set_powerup(slw.name,False)
          player_1.inventory.remove(slw.name)
          using_time = 0

    

    if player_1.collide(player_1.snake[1::]) != 0:
      player_1.die()
      FPS_backup = 20
      FPS = 20
      ss = 0
      player_1.start(center)
    if player_1.collide([f.rect]) != 0:
      ss+=10
      if 'boost' in player_1.get_powerups():
          FPS_backup+=2
      FPS += 0.5
      player_1.add()
      food_pos = (random.randint(x_off,screen.get_width()-(x_off+100)), random.randint(y_off,screen.get_height()-(y_off+100)))
      f = Food(food_pos,screen)
    if player_1.collide([tp.rect,slw.rect]) != 0:
      collision_pos = player_1.collide([tp.rect,slw.rect])
      if len(player_1.inventory) < player_1.inventory_limit:
        if tp.rect.left == collision_pos[0] and tp.rect.right == collision_pos[1] and tp.rect.top == collision_pos[2] and tp.rect.bottom == collision_pos[3] and tp.name not in player_1.inventory and tp.powerup_spawned():
          player_1.inventory.append(tp.name)
          player_1.active_powers.update({tp.name:False})
          tp.set_position((random.randint(x_off,screen.get_width()-x_off), random.randint(y_off,screen.get_height()-y_off)))
          tp.sp = False; time_TP = 0
        if slw.rect.left == collision_pos[0] and slw.rect.right == collision_pos[1] and slw.rect.top == collision_pos[2] and slw.rect.bottom == collision_pos[3] and slw.name not in player_1.inventory and slw.powerup_spawned():
          player_1.inventory.append(slw.name)
          player_1.active_powers.update({slw.name:False})
          slw.set_position((random.randint(x_off,screen.get_width()-x_off), random.randint(y_off,screen.get_height()-y_off)))
          slw.sp = False; time_SLW = 0; using_time = 0
    screen.fill((0,0,0))
    screen.blit(score_srf,((screen.get_width()//2)-100/2,100,300,100))
    screen.blit(boost_srf,((screen.get_width()//2)-100/2,screen.get_height()-140,80,100))
    if (len(player_1.inventory) >= 2):
      try:
        screen.blit(power_1_srf,(200,(screen.get_height()-140),80,100))
      except NameError:
        ...
    if (len(player_1.inventory) == 3):
      try:
        screen.blit(power_2_srf,(screen.get_width()-(x_off+power_2_srf.get_width()),(screen.get_height()-140),80,100))
      except NameError:
        ...
    f.draw()
    tp.draw()
    slw.draw()
    player_1.draw()
    pg.display.update()
    player_1.move(x_off,y_off)

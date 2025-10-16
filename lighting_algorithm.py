import pygame as pg
import os,sys
from la_functions import make_grid,proportioning,make_rect,most_distant_freefielded_insight,draw_shadow
import numpy as np

pg.init()
pg.display.init()
clock = pg.time.Clock()
FPS = 60
active = False

screen = pg.display.set_mode((680,500),pg.RESIZABLE)
sight_field = pg.Surface((680,500),pg.RESIZABLE).convert_alpha()
sight_field.fill((60,60,60,60))
old_w = screen.get_width(); old_h = screen.get_height()
quads = []
visible_q = []
prev_pressed = [False,False,False]
f=False
visible = False
fil = open('sight_radius.config','r')
lines = fil.read().split('\n')
fullscreen = False
if lines[0] == 'True':
    s_r_config = screen.get_width()+10000 #se trova True sight_r = fullscreen
    fullscreen = True
else:
    s_r_config = int(lines[1])
fil.close()
screen.fill((60,60,60))

#GAME LOOP
if __name__ == "__main__":
    while True:
        #clock.tick(FPS)
        #EVENT HANDLER
        for ev in pg.event.get():
            if ev.type == pg.QUIT:
                pg.quit()
                sys.exit()
            if ev.type == pg.VIDEORESIZE:
                old_w = screen.get_width(); old_h = screen.get_height() #variabili dedicate al resizing
                screen = pg.display.set_mode((ev.w,ev.h),pg.RESIZABLE)
                sight_field = pg.Surface((ev.w,ev.h),pg.RESIZABLE).convert_alpha()
                old_w = screen.get_width(); old_h = screen.get_height()
                #grid = make_grid(screen,100)
            if ev.type == pg.KEYDOWN:
                if ev.key == pg.K_LCTRL:
                    if not active:
                        active = True
                    elif active:
                        active = False
                if ev.key == pg.K_LALT:
                    if not visible:
                        visible = True
                    else:
                        visible = False
                if ev.key == pg.K_DELETE:
                    quads.clear()
                    visible_q.clear()
            if ev.type == pg.MOUSEBUTTONDOWN:
                for index in range(0,len(pg.mouse.get_pressed())):
                    if pg.mouse.get_pressed()[index] and index == 0:
                        (shape_left,shape_top) = pg.mouse.get_pos()
                        prev_pressed[0] = True
                        f = True
                    elif pg.mouse.get_pressed()[index] and index == 2:
                        try:
                            m_p = pg.mouse.get_pos()
                            for s in quads:
                                if m_p[0] in range(s.left,s.right) and m_p[1] in range(s.top,s.bottom):
                                    quads.remove(s)
                            for s in visible_q:
                                if m_p[0] in range(s.left,s.right) and m_p[1] in range(s.top,s.bottom):
                                    visible_q.remove(s)
                        except IndexError:
                            ...
                if not f:
                    #evento movimento rotellina
                    pass #non faccio nulla (equivalente ad un ...)
                f = False
            if ev.type == pg.MOUSEBUTTONUP:
                for index in range(0,len(pg.mouse.get_pressed())):
                    if pg.mouse.get_pressed()[index] == 0 and prev_pressed[index] and index == 0:
                        (shape_right,shape_bottom) = pg.mouse.get_pos()
                        if not visible:
                            quads.append(make_rect(shape_left,shape_right,shape_top,shape_bottom))
                        else:
                            visible_q.append(make_rect(shape_left,shape_right,shape_top,shape_bottom))
                    prev_pressed[index] = False
        #EVENT HANDLER END

        if not active:
            screen.fill((60,60,60))
        else:
            screen.fill((100,100,40))
        if active:
            #cerchio trasparente
            sight_field.fill((0,0,0))
            sight_r = s_r_config #circle's radius
            pg.draw.circle(sight_field,(255,255,255,0),pg.mouse.get_pos(),sight_r,0)
            #fine cerchio trasparente
            for s in visible_q:
                pg.draw.rect(screen,(0,255,255),s)
            for s in quads:
                rec_i = pg.draw.rect(screen,(0,0,255),s,0)
                player_pos = pg.mouse.get_pos()
                draw_shadow(screen,rec_i,player_pos,sight_r,quads)
        # displaying phase
        if active:
            screen.blit(sight_field,sight_field.get_rect())
        if not active:
            for s in quads:
                pg.draw.rect(screen,(0,0,255),s)
            for s in visible_q:
                pg.draw.rect(screen,(0,255,255),s)
        pg.display.update()
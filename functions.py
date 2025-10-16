import pygame as pg
import os,sys

pg.init()
pg.display.init()

QUIT = pg.font.SysFont('Comic Sans MS',20,1)
pause = pg.font.SysFont('Comic Sans MS',20,1)

def paused(flag,screen):
    pg.mouse.set_visible(False)
    while flag:
        pause_symbol1 = pg.draw.rect(screen,(0,0,0),(screen.get_width()//2-50,screen.get_height()//2,20,100),0)
        pause_symbol2 = pg.draw.rect(screen,(0,0,0),(screen.get_width()//2+50,screen.get_height()//2,20,100),0)
        quit_srf = QUIT.render("ESC to exit the game",1,(28,112,28))
        pause_srf = pause.render("Pause to Unpause/Pause the game",1,(28,112,28))
        for event in pg.event.get():
            if event.type == pg.QUIT:
                pg.quit()
                sys.exit()
            if event.type == pg.KEYDOWN:
                if event.key == pg.K_ESCAPE:
                    pg.quit()
                    sys.exit()
                if event.key == pg.K_PAUSE:
                    pg.mouse.set_visible(True)
                    flag = False
        screen.fill((40,40,40))
        screen.blit(quit_srf,(280,100,80,100))
        screen.blit(pause_srf,(screen.get_width()//2+300,100,80,100))
        pg.display.update([pause_symbol1,pause_symbol2])
        pg.display.update(280,100,quit_srf.get_width(),quit_srf.get_height())
        pg.display.update(screen.get_width()//2+300,100,pause_srf.get_width(),pause_srf.get_height())
import pygame as pg
class game_Object():
    def __init__(self,o_ID):
        self.ID = o_ID
        return
    
    def check_ID(self):
        return self.ID

class Snake(game_Object):
    def __init__(self,surface,position,dimension,direction=[1,0],color=(255,255,255)):
        self.snake = []
        self.game_screen = surface
        self.dim = dimension
        self.sd = direction
        self.dr = self.dim + 3
        self.color = color
        self.active_powers = {}
        self.inventory = []
        self.inventory_limit = 3
        self.start(position)
        game_Object.__init__(self,1)

    def start(self,position):
        sp_w = position[0]; sp_h = position[1]
        sn = self.dim; dr = self.dr
        self.snake.append(pg.Rect(sp_w,sp_h,sn,sn))
        self.snake.append(pg.Rect(sp_w,sp_h+(dr+5)*1,sn,sn))
        self.snake.append(pg.Rect(sp_w,sp_h+(dr+5)*2,sn,sn))
        self.snake.append(pg.Rect(sp_w,sp_h+(dr+5)*3,sn,sn))
        self.inventory.append('boost')
        self.active_powers.update({'boost':False})
        
    
    #POWER UPS
    def set_powerup(self,power,state):
        self.active_powers.update({power:state})
    def get_powerups(self):
        return [p for p in self.active_powers.keys() if self.active_powers.get(p) == True]

    #SNAKE METHODS
    def add(self):
        dr = self.dr; sn = self.dim
        new_S = pg.Rect(self.snake[-1].left+(self.sd[0]*dr),self.snake[-1].top+(self.sd[1]*dr),sn,sn)
        self.snake.append(new_S)
        new_S = pg.Rect(self.snake[-1].left+(self.sd[0]*dr),self.snake[-1].top+(self.sd[1]*dr),sn,sn)
        self.snake.append(new_S)
        new_S = pg.Rect(self.snake[-1].left+(self.sd[0]*dr),self.snake[-1].top+(self.sd[1]*dr),sn,sn)
        self.snake.append(new_S)
    def calculate_next(self,prev_S,iter):
        if iter < len(self.snake):
            next_S = self.snake[iter]
            self.snake[iter] = prev_S
            self.calculate_next(next_S,iter+1)
        ##
        else:
            return
        return
    def move(self,x_off,y_off):
        sn = self.dim; dr = self.dr
        if self.snake[0].right > self.game_screen.get_width()-(5+x_off):
            self.calculate_next(pg.Rect(10+x_off,self.snake[0].top+(self.sd[1]*dr+1),sn,sn),0)
        elif self.snake[0].left < (5+x_off):
            self.calculate_next(pg.Rect(self.game_screen.get_width()-(10+x_off+20),self.snake[0].top+(self.sd[1]*dr+1),sn,sn),0)
        
        elif self.snake[0].top < (5+y_off):
            self.calculate_next(pg.Rect(self.snake[0].left+(self.sd[0]*dr+1),self.game_screen.get_height()-(10+y_off+20),sn,sn),0)
        elif self.snake[0].bottom > self.game_screen.get_height()-(5+y_off):
            self.calculate_next(pg.Rect(self.snake[0].left+(self.sd[0]*dr+1),10+y_off,sn,sn),0)
        
        else:
            self.calculate_next(pg.Rect(self.snake[0].left+(self.sd[0]*dr+1),self.snake[0].top+(self.sd[1]*dr+1),sn,sn),0)
    def replace(self,x,y):
        sn = self.dim
        self.calculate_next(pg.Rect(x,y,sn,sn),0)
    
    def collide(self,object:'pg.Rect or his list'):
        x0 = self.snake[0].left; xF = self.snake[0].right
        y0 = self.snake[0].top; yF = self.snake[0].bottom
        for o in object:
            if (x0 in range(o.left,o.right) or xF in range(o.left,o.right)) and (y0 in range(o.top,o.bottom) or yF in range(o.top,o.bottom)):
                return [o.left,o.right,o.top,o.bottom]
        return 0
    def die(self):
        self.snake.clear()
        self.inventory.clear()
        self.active_powers.clear()
    def draw(self):
        for s in self.snake:
            pg.draw.rect(self.game_screen,self.color,s,0)
        return

class Food(game_Object):
    def __init__(self,position,surface):
        self.center = position
        self.game_screen = surface
        self.color = (255,0,0)
        self.size = 10
        self.rect = pg.Rect(position[0],position[1],self.size,self.size)
        game_Object.__init__(self,2)
    
    def draw(self):
        pg.draw.rect(self.game_screen,self.color,self.rect,0)
        return

class Power(game_Object):
    def __init__(self,surface,position,duration,name,color):
        self.color = color
        self.center = position
        self.game_screen = surface
        self.name = name
        self.dt = duration
        self.at = duration/4
        self.size = 10
        self.sp = False
        self.rect = pg.Rect(self.center[0],self.center[1],10,10)
        game_Object.__init__(self,3)

    def draw(self):
        if self.sp:
            pg.draw.rect(self.game_screen,self.color,self.rect,0)
        return
    def powerup_spawned(self):
        return self.sp
    def spawn(self):
        self.sp = True
    def set_position(self,position):
        self.center = position
        self.rect = pg.Rect(self.center[0],self.center[1],10,10)
        return
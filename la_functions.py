import pygame as pg
import numpy as np


def make_grid(surface, dimension):
    width = surface.get_width()
    height = surface.get_height()
    while True:
        remaining_x = width%dimension
        remaining_y = height%dimension
        if remaining_x == 0 and remaining_y == 0:
            break
        else:
            dimension+=1
    print(dimension)
    piece_x = width/dimension
    print(piece_x)
    piece_y = height/dimension
    print(piece_y)
    
    return [piece_x,piece_y]

def proportioning(shape,surface,old_surface):
    old_w = old_surface[0]; old_h = old_surface[1]
    r_old_w = shape.right - shape.left; r_old_h = shape.bottom - shape.top
    proportionFactor_x = surface.get_width()/old_w; proportionFactor_y = surface.get_height()/old_h
    return pg.Rect(shape.left,shape.top,r_old_w*proportionFactor_x,r_old_h*proportionFactor_y)

def make_rect(left,right,top,bottom):
    if left > right:
        change = left
        left = right
        right = change
    if top > bottom:
        change = top
        top = bottom
        bottom = change
    
    x_size = right-left; y_size = bottom-top

    return pg.Rect(left,top,x_size,y_size)

def most_distant_freefielded_insight(r,p_p,s_r,r_list): #RICORDA DI RITORNARE IL BOTTOM COME RESULT[0] E IL TOP COME RESULT[1]
                                                        #IN CASO DI ENTRAMBI SULLO STESSO LATO(BOT,BOT//TOP,TOP) RESULT[0] RAPPRESENTA A SX
    result0 = 0; result1 = 0 #entrambi vettori di 2 elementi
    #primo check: creazione della in sight list:
    insight_list = []
    #TOP LEFT:
    top_left = [r.left,r.top]
    if calculate_distance(top_left,p_p) <= s_r:
        insight_list.append(top_left)
    #BOTTOM LEFT:
    bottom_left = [r.left,r.bottom]
    if calculate_distance(bottom_left,p_p) <= s_r:
        insight_list.append(bottom_left)
    #TOP RIGHT:
    top_right = [r.right,r.top]
    if calculate_distance(top_right,p_p) <= s_r:
        insight_list.append(top_right)
    #BOTTOM RIGHT:
    bottom_right = [r.right,r.bottom]
    if calculate_distance(bottom_right,p_p) <= s_r:
        insight_list.append(bottom_right)
    if len(insight_list)<2:
        if p_p[0] <= r.left:
            if p_p[1] <= r.top:
                #sono in alto a sinistra
                point1 = [r.left,p_p[1]+s_r]
                point2 = [p_p[0]+s_r,r.top]
                return [point1,point2]
            elif p_p[1] >= r.bottom:
                #sono in basso a sinistra (cambia ordine se non worka)
                point1 = [p_p[0]+s_r,r.bottom]
                point2 = [r.left,p_p[1]-s_r]
                return [point1,point2]
            else:
                #sono a sinistra:
                point1 = [r.left,r.top]
                point2 = [r.left,r.bottom]
                return [point1,point2]
        elif p_p[0] >= r.right:
            if p_p[1] <= r.top:
                #sono in alto a destra
                point1 = [r.right,p_p[1]+s_r]
                point2 = [p_p[0]-s_r,r.top]
                return [point1,point2]
            elif p_p[1] >= r.bottom:
                #sono in basso a destra (cambia ordine se non worka)
                point1 = [p_p[0]-s_r,r.bottom]
                point2 = [r.right,p_p[1]-s_r]
                return [point1,point2]
            else:
                #sono a destra:
                point1 = [r.right,r.top]
                point2 = [r.right,r.bottom]
                return [point1,point2]
        else:
            if p_p[1] <= r.top:
                #sono in alto
                point1 = [r.left,r.top]
                point2 = [r.right,r.top]
                return [point1,point2]
            else:
                #sono in basso
                point1 = [r.left,r.bottom]
                point2 = [r.right,r.bottom]
                return [point1,point2]
    #secondo check: riconoscimento del top e del bottom:
    if p_p[1] >= r.bottom: #se è sotto il segmento basso del rettangolo prende bottom_distant
        if p_p[0] <= r.left: #se sono qui allora il giocatore è sotto e a sinistra rispetto il rettangolo
            if top_left in insight_list:
                result1 = top_left
            else:
                y_point = p_p[1] - s_r
                result1 = [r.left,y_point]
            if bottom_right in insight_list:
                result0 = bottom_right # se posso vederlo il vertice è bottom_right
            else:
                x_point = p_p[0] + s_r
                result0 = [x_point,r.bottom] # se non vedo bottom_right allora prendo bottom_left
# SE RESULT[0] E RESULT[1] COINCIDONO VUOL DIRE CHE SONO TROPPO DISTANTE PER DISEGNARE UN'OMBRA!!
        elif p_p[0] >= r.right: #qui ci troviamo sotto a destra
            if top_right in insight_list:
                result1 = top_right
            else:
                y_point = p_p[1] - s_r
                result1 = [r.right,y_point]
            if bottom_left in insight_list:
                result0 = bottom_left
            else:
                x_point = p_p[0] - s_r
                result0 = [x_point,r.bottom] #da correggere con il punto alla coordinata y=r.bottom avente per x=(y-q)/m 
        else: #qui siamo proprio sotto => vede solo bottom left e bottom right
            return [bottom_left, bottom_right]
    elif p_p[1] <= r.top: #caso in cui siamo sul lato superiore del rettangolo
        if p_p[0] <= r.left: #in alto a sinistra rispetto il rettangolo
            if bottom_left in insight_list:
                result0 = bottom_left
            else:
                y_point = p_p[1] + s_r
                result0 = [r.left,y_point]
            if top_right in insight_list:
                result1 = top_right
            else:
                x_point = p_p[0] + s_r
                result1 = [x_point,r.top]
        elif p_p[0] >= r.right: #in alto a destra
            if bottom_right in insight_list:
                result0 = bottom_right
            else:
                y_point = p_p[1] + s_r
                result0 = [r.right,y_point]
            if top_left in insight_list:
                result1 = top_left
            else:
                x_point = p_p[0] - s_r
                result1 = [x_point,r.top]
        else: #siamo proprio sopra
            return [top_left,top_right]
    else: #siamo proprio sul lato
        if p_p[0] <= r.left: # lato sinistro
            return [bottom_left,top_left]
        else: # lato destro
            return [bottom_right, top_right]
    #terzo check: controllo di ostacoli nel percorso del raggio (non c'ho voglia tutta matematica è)


    return [result0,result1]

def calculate_distance(pointA, pointB):
    distance_vec = [ pointB[0]-pointA[0], pointB[1]-pointA[1] ]
    return np.sqrt( (distance_vec[0]*distance_vec[0]) + (distance_vec[1]*distance_vec[1]) )

def resize(quads,screen,old_w,old_h):
    for i in range(0,len(quads)):
        quads[i] = proportioning(quads[i],screen,(old_w,old_h))

def draw_shadow(screen,rec_i,player_pos,sight_r,quads):
    vertex_low,vertex_top = most_distant_freefielded_insight(rec_i,player_pos,sight_r,quads) #del tipo [x,y]
    if vertex_low is not None and vertex_top is not None:
        x2 = x1 = y1 = y2 = 0
        if vertex_low[0] != player_pos[0] or vertex_low[1] != player_pos[1]:
            try:
                m = (vertex_low[1] - player_pos[1]) / (vertex_low[0]-player_pos[0])
                angle = np.arctan(m)
                q = ((vertex_low[0]*player_pos[1]) - (player_pos[0]*vertex_low[1])) / (vertex_low[0]-player_pos[0])
                if player_pos[0] <= rec_i.left:
                    x1 = player_pos[0] + (sight_r*np.cos(angle))
                elif player_pos[0] >= rec_i.right:
                    x1 = player_pos[0] - (sight_r*np.cos(angle))
                else:
                    x1 = player_pos[0] - (sight_r*np.cos(angle))
                y1 = m*x1+q
            except ZeroDivisionError:
                if player_pos[0] <= rec_i.left:
                    if player_pos[1] <= rec_i.top:
                        #sono in alto a sinistra
                        x1 = rec_i.left; y1 = screen.get_height()
                    elif player_pos[1] >= rec_i.bottom:
                        #sono in basso a sinistra (cambia ordine se non worka)
                        x1 = rec_i.left; y1 = 0
                    else:
                        #sono a sinistra
                        x1 = screen.get_width(); y1 = player_pos[1]
                elif player_pos[0] >= rec_i.right:
                    if player_pos[1] <= rec_i.top:
                        #sono in alto a destra
                        x1 = rec_i.right; y1 = screen.get_height()
                    elif player_pos[1] >= rec_i.bottom:
                        #sono in basso a destra (cambia ordine se non worka)
                        x1 = rec_i.right; y1 = 0
                    else:
                        #sono a destra:
                        x1 = 0; y1 = player_pos[1]
                else:
                    if player_pos[1] <= rec_i.top:
                        #sono in alto
                        y1 = screen.get_height(); x1 = player_pos[0]
                    else:
                        #sono in basso
                        y1 = 0; x1 = player_pos[0]
        if vertex_top[0] != player_pos[0] or vertex_top[1] != player_pos[1]:
            try:
                m = (vertex_top[1] - player_pos[1]) / (vertex_top[0]-player_pos[0])
                angle = np.arctan(m)
                q = ((vertex_top[0]*player_pos[1]) - (player_pos[0]*vertex_top[1])) / (vertex_top[0]-player_pos[0])
                if player_pos[0] <= rec_i.left:
                    x2 = player_pos[0] + (sight_r*np.cos(angle))
                elif player_pos[0] >= rec_i.right:
                    x2 = player_pos[0] - (sight_r*np.cos(angle))
                else:
                    x2 = player_pos[0] + (sight_r*np.cos(angle))
                y2 = m*x2+q
            except ZeroDivisionError:
                if player_pos[0] <= rec_i.left:
                    if player_pos[1] <= rec_i.top:
                        #sono in alto a sinistra
                        x2 = rec_i.left; y2 = screen.get_height()
                    elif player_pos[1] >= rec_i.bottom:
                        #sono in basso a sinistra (cambia ordine se non worka)
                        x2 = rec_i.left; y2 = 0
                    else:
                        #sono a sinistra
                        x2 = screen.get_width(); y2 = player_pos[1]
                elif player_pos[0] >= rec_i.right:
                    if player_pos[1] <= rec_i.top:
                        #sono in alto a destra
                        x2 = rec_i.right; y2 = screen.get_height()
                    elif player_pos[1] >= rec_i.bottom:
                        #sono in basso a destra (cambia ordine se non worka)
                        x2 = rec_i.right; y2 = 0
                    else:
                        #sono a destra:
                        x2 = 0; y2 = player_pos[1]
                else:
                    if player_pos[1] <= rec_i.top:
                        #sono in alto
                        y2 = screen.get_height(); x2 = player_pos[0]
                    else:
                        #sono in basso
                        y2 = 0; x2 = player_pos[0]
        else:
            x2 = None; y2 = None
        if x1 is not None and y1 is not None and x2 is not None and y2 is not None:
            if player_pos[0] >= rec_i.right and player_pos[1] >= rec_i.bottom: # se sono in basso a destra:
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2], [0,y2], [0,y1], [x1,y1], vertex_low, [rec_i.right,rec_i.bottom] , vertex_top ])
            elif player_pos[0] >= rec_i.right and player_pos[1] <= rec_i.top: # se sono in alto a destra
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2], [0,y2], [0,y1], [x1,y1], vertex_low, [rec_i.right,rec_i.top] , vertex_top ])
            elif player_pos[0] <= rec_i.left and player_pos[1] >= rec_i.bottom: #se sono in basso a sinistra
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2], [screen.get_width(),y2], [screen.get_width(),y1], [x1,y1], vertex_low, [rec_i.left,rec_i.bottom] , vertex_top ])
            elif player_pos[0] <= rec_i.left and player_pos[1] <= rec_i.top: #se sono in alto a sinistra
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2], [screen.get_width(),y2], [screen.get_width(),y1], [x1,y1], vertex_low, [rec_i.left,rec_i.top] , vertex_top ])
            elif player_pos[0] <= rec_i.right and player_pos[0] >= rec_i.left and player_pos[1] <= rec_i.top: #se sono in alto
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2],[x2,screen.get_height()], [x1,screen.get_height()], [x1,y1], vertex_low, vertex_top ])
            elif player_pos[0] >= rec_i.left and player_pos[0] <= rec_i.right and player_pos[1] >= rec_i.bottom: #se sono in basso
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2], [x2,0], [x1,0], [x1,y1], vertex_low, vertex_top ])
            elif player_pos[1] <= rec_i.bottom and player_pos[1] >= rec_i.top and player_pos[0] <= rec_i.left: #se sono a sinistra:
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2], [screen.get_width(),y2], [screen.get_width(),y1], [x1,y1], vertex_low, vertex_top ])
            elif player_pos[1] <= rec_i.bottom and player_pos[1] >= rec_i.top and player_pos[0] >= rec_i.right: #sono a destra
                pg.draw.polygon(screen,(0,0,0),[ [x2,y2], [0,y2], [0,y1], [x1,y1], vertex_low, vertex_top ])
            else:
                ...
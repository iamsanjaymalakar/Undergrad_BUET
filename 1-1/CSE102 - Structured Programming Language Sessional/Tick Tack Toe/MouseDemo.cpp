# include "iGraphics.h"
#include<windows.h>

// Gloabal Variables
int mouseX,mouseY;
int fscreen=0; // 0 Opening Window , 1 Single Player, 2 Multiplayer, 3 End Game , 4 SP Move Choice , 5 Timer Choice, 6 Help
int fboard[10]= {1,1,1,1,1,1,1,1,1,1};
char board[10]= {' ',' ',' ',' ',' ',' ',' ',' ',' ',' '};
int showXO[10]= {0,0,0,0,0,0,0,0,0,0};
int selection,player=1,winner=0,gamePosition=-1; // winner 1 X wins, 2 O wins, 3 Game Draw,4 Computer 0 No result
int moveNum=0;
int xoxo[2];
int gif1=0,gifb;
int counterp1=0,counterp2=0,counterdraw=0;
int i,j=0;
int t,t1,t2,t3,countDown=30;
int level=1; //level 1 easy, 2 medium, 3 hard
int c1=0; //c1 is for checking the difference of moveNum
int yesno=3; // 1 nothing, 2 Yes, 3 No
int gamemode=0; // 1 Sp, 2 MP
int m=0;
int line=-1;
int sflag=1; // 0 for off and 1 for on
int ss=1,sw=0,sl=0,sd=0;
int scount=0;


struct point
{
    int x;
    int y;
};
struct point co[10]=
{
    {0,0},
    {200,500},
    {366,500},
    {536,500},
    {200,334},
    {366,334},
    {536,334},
    {200,167},
    {366,167},
    {536,167}
};



int gameOver(char box[]);
int singlePlayer();
void mouseMove();
void sBoard();
void multiPlayer();
void drawO(int a,int b);
void drawX(int a,int b);
int miniMax(char box[], int player);
void showNum(int n,int x1,int y1,int x2,int y2,int font);
void init();
void timer(void);
void drawLine(int line);
void Pause(void);
void gif(void);
void lineChecker(char box[]);
void gifbb(void);


void iDraw()
{
    if(ss && sflag)
    {
        PlaySound("sound\\Start_Music.wav",NULL,SND_ASYNC|SND_LOOP);
        ss=0;
    }
    if(sd && sflag)
    {
        PlaySound("sound\\Draw.wav",NULL,SND_ASYNC|SND_LOOP);
        sd=0;
    }
    if(sl && sflag)
    {
        PlaySound("sound\\Loss.wav",NULL,SND_ASYNC|SND_LOOP);
        sl=0;
    }
    if(sw && sflag)
    {
        PlaySound("sound\\Win.wav",NULL,SND_ASYNC|SND_LOOP);
        sw=0;
    }

    if(fscreen==0)  // Initial screen where player selects game mode
    {
        iClear();

        /*if(sflag==0)
        {
            PlaySound("sound\\Start_Music.wav",NULL,SND_ASYNC);
        }*/

        iSetColor(0,0,0);
        iFilledRectangle(0,0,900,500);
        if(gif1==1)
            iShowBMP(200, 310, "images\\ttt1.bmp");
        else if(gif1==2)
            iShowBMP(200, 310, "images\\ttt2.bmp");
        else if(gif1==3)
            iShowBMP(200, 310, "images\\ttt3.bmp");
        else if(gif1==4)
            iShowBMP(200, 310, "images\\ttt4.bmp");
        else if(gif1==5)
            iShowBMP(200, 310, "images\\ttt5.bmp");
        else if(gif1==6 || gif1==0)
            iShowBMP(200, 310, "images\\ttt6.bmp");
        iSetColor(255,255,255);
        iRectangle(230,220,440,90);
        iShowBMP(235,220, "images\\sp.bmp");
        iSetColor(255,255,255);
        iRectangle(230,120,440,90);
        iShowBMP(255,140, "images\\mp.bmp");
        iShowBMP(0,0,"images\\left.bmp");
        iShowBMP(800,0,"images\\right.bmp");
        iSetColor(255,255,255);
        iRectangle(460,25,180,80);
        iShowBMP(480,38, "images\\exit.bmp");
        iSetColor(255,255,255);
        iRectangle(260,25,180,80);
        iShowBMP(295,35, "images\\help.bmp");


    }
    else if(fscreen==4)  //will you move first?
    {
        iClear();
        iSetColor(0,0,0);
        iFilledRectangle(0,0,900,500);
        if(gif1==1)
            iShowBMP(200, 310, "images\\ttt1.bmp");
        else if(gif1==2)
            iShowBMP(200, 310, "images\\ttt2.bmp");
        else if(gif1==3)
            iShowBMP(200, 310, "images\\ttt3.bmp");
        else if(gif1==4)
            iShowBMP(200, 310, "images\\ttt4.bmp");
        else if(gif1==5)
            iShowBMP(200, 310, "images\\ttt5.bmp");
        else if(gif1==6 || gif1==0)
            iShowBMP(200, 310, "images\\ttt6.bmp");
        iShowBMP(0,0,"images\\left.bmp");
        iShowBMP(800,0,"images\\right.bmp");
        iShowBMP(220,230, "images\\wymf.bmp");
        if(yesno==1)
            iShowBMP(290,90, "images\\yesno.bmp");
        else if(yesno==2)
            iShowBMP(290,90, "images\\yesnoyes.bmp");
        else if(yesno==3)
            iShowBMP(290,90, "images\\yesnono.bmp");
        iShowBMP(215,35, "images\\backnext.bmp");
    }
    else if(fscreen==5)  // Timer
    {
        iClear();
        iSetColor(0,0,0);
        iFilledRectangle(0,0,900,500);
        if(gif1==1)
            iShowBMP(200, 310, "images\\ttt1.bmp");
        else if(gif1==2)
            iShowBMP(200, 310, "images\\ttt2.bmp");
        else if(gif1==3)
            iShowBMP(200, 310, "images\\ttt3.bmp");
        else if(gif1==4)
            iShowBMP(200, 310, "images\\ttt4.bmp");
        else if(gif1==5)
            iShowBMP(200, 310, "images\\ttt5.bmp");
        else if(gif1==6 || gif1==0)
            iShowBMP(200, 310, "images\\ttt6.bmp");
        iShowBMP(0,0,"images\\left.bmp");
        iShowBMP(800,0,"images\\right.bmp");
        iShowBMP(220,280, "images\\time.bmp");
        if(level==1)
            iShowBMP(165,180, "images\\30.bmp");
        else if(level==2)
            iShowBMP(165,180, "images\\20.bmp");
        else if(level==3)
            iShowBMP(165,180, "images\\10.bmp");
        iShowBMP(450,50, "images\\play.bmp");
        iShowBMP(250,70, "images\\backs.bmp");
    }
    else if(fscreen==1) // Single Player
    {
        iClear();
        /*if(sflag)
        {
            PlaySound("sound\\Board_Music.wav",NULL,SND_ASYNC);
        }*/
        sBoard();
        lineChecker(board);
        iShowBMP(45,390, "images\\cpu.bmp");
        showNum(counterp1,50,350,88,350,3);
        iShowBMP(15,250, "images\\player.bmp");
        showNum(counterp2,50,215,88,215,3);
        iShowBMP(35,120, "images\\draw.bmp");
        showNum(counterdraw,50,80,88,80,3);
        iShowBMP(720,375,"images\\timer.bmp");
        if(countDown<4)
            showNum(countDown,740,320,780,320,5);
        else
            showNum(countDown,740,320,780,320,4);
        iShowBMP(715,85,"images\\back.bmp");
        iShowBMP(720,20,"images\\exitdim.bmp");
        if(player==1 && gameOver(board)==-1)
        {
            singlePlayer();

        }

    }
    else if(fscreen==2) // Multiplayer
    {
        iClear();
        sBoard();
        lineChecker(board);
        if(player==1)
        {
            iShowBMP(15,380, "images\\player1.bmp");
            showNum(counterp1,50,345,88,345,1);
        }
        else
        {
            iShowBMP(15,380, "images\\player1dim.bmp");
            showNum(counterp1,50,345,88,345,2);
        }
        if(player==2)
        {
            iShowBMP(15,250, "images\\player2.bmp");
            showNum(counterp2,50,215,88,215,1);
        }
        else
        {
            iShowBMP(15,250, "images\\player2dim.bmp");
            showNum(counterp2,50,215,88,215,2);
        }
        iShowBMP(35,120, "images\\draw.bmp");
        showNum(counterdraw,50,80,88,80,3);
        iShowBMP(720,375,"images\\timer.bmp");
        if(countDown<4)
            showNum(countDown,740,320,780,320,5);
        else
            showNum(countDown,740,320,780,320,4);
        iShowBMP(715,85,"images\\back.bmp");
        iShowBMP(720,20,"images\\exitdim.bmp");
        gamePosition=gameOver(board);
    }
    else if(fscreen==3) // Ending Screen
    {
        iClear();
        iSetColor(0,0,0);
        iFilledRectangle(0,0,900,500);
        if(gifb==0)
        {
            iShowBMP(200,400-j,"images\\star1.bmp");
            iShowBMP(400,500-j,"images\\star1.bmp");
            iShowBMP(700,300-j,"images\\star1.bmp");
            iShowBMP(800,800-j,"images\\star1.bmp");
            iShowBMP(500,1100-j,"images\\star1.bmp");
        }
        else if(gifb==1)
        {
            iShowBMP(200,400-j,"images\\star2.bmp");
            iShowBMP(400,500-j,"images\\star2.bmp");
            iShowBMP(700,300-j,"images\\star2.bmp");
            iShowBMP(800,800-j,"images\\star2.bmp");
            iShowBMP(500,1100-j,"images\\star2.bmp");
        }
        else if(gifb==2)
        {
            iShowBMP(200,400-j,"images\\star3.bmp");
            iShowBMP(400,500-j,"images\\star3.bmp");
            iShowBMP(700,300-j,"images\\star3.bmp");
            iShowBMP(800,800-j,"images\\star3.bmp");
            iShowBMP(500,1100-j,"images\\star3.bmp");
        }
        j++;
        if(j==1000)
            j=0;
        if(winner==3)
        {
            if(!scount)
            {
                sd=1;
                scount=1;
            }

            if(gifb==0)
            {
                iShowBMP(250,200,"images\\draw1.bmp");
            }
            else if(gifb==1)
            {
                iShowBMP(250,200,"images\\draw2.bmp");
            }
            else if(gifb==2)
            {
                iShowBMP(250,200,"images\\draw3.bmp");
            }

        }
        else if((winner==1 || player==2) && gamemode==2)
        {
            if(!scount)
            {
                sw=1;
                scount=1;
            }
            if(gifb==0)
            {
                iShowBMP(140,200,"images\\p1win1.bmp");
            }
            else if(gifb==1)
            {
                iShowBMP(140,200,"images\\p1win2.bmp");
            }
            else if(gifb==2)
            {
                iShowBMP(140,200,"images\\p1win3.bmp");
            }
        }
        else if((winner==2 || player==1) && gamemode==2)
        {
            if(!scount)
            {
                sw=1;
                scount=1;
            }
            if(gifb==0)
            {
                iShowBMP(140,200,"images\\p2win1.bmp");
            }
            else if(gifb==1)
            {
                iShowBMP(140,200,"images\\p2win2.bmp");
            }
            else if(gifb==2)
            {
                iShowBMP(140,200,"images\\p2win3.bmp");
            }
        }
        else if((winner==1 || player==2) && gamemode==1)
        {
            if(!scount)
            {
                sl=1;
                scount=1;
            }
            if(gifb==0)
            {
                iShowBMP(140,200,"images\\pcwin1.bmp");
            }
            else if(gifb==1)
            {
                iShowBMP(140,200,"images\\pcwin2.bmp");
            }
            else if(gifb==2)
            {
                iShowBMP(140,200,"images\\pcwin3.bmp");
            }
        }
        else if((winner==2 || player==1) && gamemode==1)
        {
            if(!scount)
            {
                sw=1;
                scount=1;
            }
            if(gifb==0)
            {
                iShowBMP(140,200,"images\\pwin1.bmp");
            }
            else if(gifb==1)
            {
                iShowBMP(140,200,"images\\pwin2.bmp");
            }
            else if(gifb==2)
            {
                iShowBMP(140,200,"images\\pwin3.bmp");
            }
        }
        iShowBMP(50,420,"images\\main menu.bmp");
        iShowBMP(50,60,"images\\play again.bmp");
        iShowBMP(700,60,"images\\exitdim.bmp");
    }
    else if(fscreen==6)
    {
        iClear();
        iText(50,400,"Thanks for playing this game.",GLUT_BITMAP_8_BY_13);
        iText(50,380,"In this game, win is determined if three same symbols matches horizIn this game, win is determined ",GLUT_BITMAP_8_BY_13);
        iText(50,370,"if three similar symbols matches in a row or column or diagonal.",GLUT_BITMAP_8_BY_13);
        iText(100,150,"Developed by:",GLUT_BITMAP_8_BY_13);
        iText(50,100,"Shoumik Saha",GLUT_BITMAP_8_BY_13);
        iText(50,80,"1505059",GLUT_BITMAP_8_BY_13);
        iText(200,100,"Sanjay Malakar",GLUT_BITMAP_8_BY_13);
        iText(200,80,"1505057",GLUT_BITMAP_8_BY_13);
        iShowBMP(715,85,"images\\back.bmp");
    }
    if(sflag==1)
        iShowBMP(830,430, "volon.bmp");
    if(sflag==0)
        iShowBMP(830,430, "voloff.bmp");
}




void iMouseMove(int mx, int my)
{

}


void iMouse(int button, int state, int mx, int my)
{
    if(button == GLUT_LEFT_BUTTON && state == GLUT_DOWN)
    {
        mouseX=mx;
        mouseY=my;
        printf("%d %d\n",mx,my);

        if(mouseX>=830 && mouseX<=900 && mouseY>=430 && mouseY<=500)
        {
            sflag=(sflag==1)?0:1;

        }
        if(fscreen==1 || fscreen==2)
        {
            if(mouseX>=725 && mouseX<=882 && mouseY>=98 && mouseY<=135)
            {
                init();
                counterdraw=0;
                counterp1=0;
                counterp2=0;
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);

                }
                fscreen=0;
                ss=1;
            }
            else if(mouseX>=728 && mouseX<=882 && mouseY>=30 && mouseY<=70)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);

                }
                exit(0);
            }
        }
        if(fscreen==4)
        {
            if(mouseX>=270 && mouseX<=460 && mouseY>=164 && mouseY<=260)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);

                }
                yesno=2;
                player=2;
            }
            else if(mouseX>=460 && mouseX<=640 && mouseY>=164 && mouseY<=260)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                yesno=3;
                player=1;
            }
            else if(mouseX>=240 && mouseX<=450 && mouseY>=35 && mouseY<=140)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }

                fscreen=0;
                ss=1;
            }
            else if(mouseX>=450 && mouseX<=680 && mouseY>=35 && mouseY<=140)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                fscreen=5;
            }
        }
        else if(fscreen==5)
        {
            if(mouseX>=150 && mouseX<=355 && mouseY>=160 && mouseY<=260)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                level=3;
                countDown=10;
            }
            else if(mouseX>=355 && mouseX<=550 && mouseY>=160 && mouseY<=260)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                level=2;
                countDown=20;
            }
            else if(mouseX>=551 && mouseX<=760 && mouseY>=160 && mouseY<=260)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                level=1;
                countDown=10;
            }
            else if(mouseX>=430 && mouseX<=680 && mouseY>=55 && mouseY<=145)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                if(gamemode==1)
                    fscreen=1;
                else if(gamemode==2)
                    fscreen=2;
            }
            else if(mouseX>=245 && mouseX<=410 && mouseY>=70 && mouseY<=140)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                if(gamemode==1)
                {
                    fscreen=4;
                }
                else if(gamemode==2)
                {
                    fscreen=0;
                    ss=1;
                }

            }
        }
        else if(fscreen==0)
        {

            if(mouseX>=230 && mouseX<=670 && mouseY>=221 && mouseY<=307)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                fscreen=4;
                gamemode=1;
                printf("%d %d\n",mouseX,mouseY);
            }
            if(mouseX>=230 && mouseX<=670 && mouseY>=120 && mouseY<=210)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                fscreen=5;
                gamemode=2;
                printf("%d %d\n",mouseX,mouseY);
            }
            if(mouseX>=260 && mouseX<=440 && mouseY>=27 && mouseY<=106)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                fscreen=6;
                printf("%d %d\n",mouseX,mouseY);
            }
            if(mouseX>=460 && mouseX<=640 && mouseY>=27 && mouseY<=106)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                exit(0);
                printf("%d %d\n",mouseX,mouseY);
            }
        }
        else if(fscreen==2 && gameOver(board)==-1)
        {
            mouseMove();
            printf("%d %d %d\n",mx,my,selection);
        }
        else if(fscreen==1 && player==2 && gameOver(board)==-1)
        {
            mouseMove();
            printf("%d %d %d %d\n",mx,my,selection,moveNum);
        }
        else if(fscreen==3)
        {
            if(mouseX>=705 && mouseX<=865 && mouseY>=75 && mouseY<=110)
            {
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                exit(0);
            }
            else if(mouseX>=60 && mouseX<=235 && mouseY>=430 && mouseY<=470)
            {
                init();
                counterdraw=0;
                counterp1=0;
                counterp2=0;
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                fscreen=0;
                ss=1;
            }
            else if(mouseX>=55 && mouseX<=220 && mouseY>=70 && mouseY<=110)
            {
                init();
                if(sflag)
                {
                    PlaySound("sound\\Option_Select.wav",NULL,SND_ASYNC);
                }
                fscreen=gamemode;
            }
        }
        else if(fscreen==6)
        {
            if(mouseX>=725 && mouseX<=882 && mouseY>=98 && mouseY<=135)
            {
                ss=1;
                fscreen=0;

            }
        }
    }

}

/*
	function iKeyboard() is called whenever the user hits a key in keyboard.
	key- holds the ASCII value of the key pressed.
*/
void iKeyboard(unsigned char key)
{
    if(key== 's' || key=='S')
        fscreen=1;
    if(key=='m' || key=='M')
        fscreen=2;
    if(fscreen==3)
        if(key=='p')
        {
            init();
            fscreen=2;
            printf("%d %d\n",counterp1,counterp2);
        }

    if(key == '1')
    {
        if(fboard[1])
        {
            moveNum++;
            selection=1;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '2')
    {
        if(fboard[2])
        {
            moveNum++;
            selection=2;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '3')
    {
        if(fboard[3])
        {
            moveNum++;
            selection=3;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '4')
    {
        if(fboard[4])
        {
            moveNum++;
            selection=4;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '5')
    {
        if(fboard[5])
        {
            moveNum++;
            selection=5;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '6')
    {
        if(fboard[6])
        {
            moveNum++;
            selection=6;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '7')
    {
        if(fboard[7])
        {
            moveNum++;
            selection=7;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '8')
    {
        if(fboard[8])
        {
            moveNum++;
            selection=8;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
    }
    else if(key == '9')
    {
        if(fboard[9])
        {
            moveNum++;
            selection=9;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }

    }

    //place your codes for other keys here
}

/*
	function iSpecialKeyboard() is called whenver user hits special keys like-
	function keys, home, end, pg up, pg down, arraows etc. you have to use
	appropriate constants to detect them. A list is:
	GLUT_KEY_F1, GLUT_KEY_F2, GLUT_KEY_F3, GLUT_KEY_F4, GLUT_KEY_F5, GLUT_KEY_F6,
	GLUT_KEY_F7, GLUT_KEY_F8, GLUT_KEY_F9, GLUT_KEY_F10, GLUT_KEY_F11, GLUT_KEY_F12,
	GLUT_KEY_LEFT, GLUT_KEY_UP, GLUT_KEY_RIGHT, GLUT_KEY_DOWN, GLUT_KEY_PAGE UP,
	GLUT_KEY_PAGE DOWN, GLUT_KEY_HOME, GLUT_KEY_END, GLUT_KEY_INSERT
*/
void iSpecialKeyboard(unsigned char key)
{

    if(key == GLUT_KEY_END)
    {
        exit(0);
    }
    //place your codes for other keys here
}


void sBoard(void) // Sets up tic tac toe board
{
    iSetColor(0,0,0);
    iFilledRectangle(0,0,900,500); //setting white as background

    if(gifb==0)
    {
        iShowBMP(349,0, "images\\verti.bmp");
        iShowBMP(530,0, "images\\verti.bmp");
        iShowBMP(210,160, "images\\hori.bmp");
        iShowBMP(210,320, "images\\hori.bmp");
    }
    else if(gifb==1)
    {
        iShowBMP(349,0, "images\\verti2.bmp");
        iShowBMP(530,0, "images\\verti2.bmp");
        iShowBMP(210,160, "images\\hori2.bmp");
        iShowBMP(210,320, "images\\hori2.bmp");
    }
    else if(gifb==2)
    {
        iShowBMP(349,0, "images\\verti3.bmp");
        iShowBMP(530,0, "images\\verti3.bmp");
        iShowBMP(210,160, "images\\hori3.bmp");
        iShowBMP(210,320, "images\\hori3.bmp");
    }
    int i;

    for(i=1; i<=9; i++)
    {
        if(showXO[i]==1)
            drawX(co[i].x,co[i].y);
        else if(showXO[i]==2)
            drawO(co[i].x,co[i].y);
    }

    drawLine(line);
}


void drawX(int a,int b) // Draws X
{
    iShowBMP(a+40,b-142, "images\\x.bmp");
}

void drawO(int a,int b) // Draws O
{
    iShowBMP(a+25,b-145, "images\\o.bmp");
}

void mouseMove()
{
    if(mouseX>=200 && mouseX<=365 && mouseY>=335 && mouseY<=500)
    {
        if(fboard[1])
        {
            moveNum++;
            selection=1;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
    else if(mouseX>=366 && mouseX<=535 && mouseY>=335 && mouseY<=500)
    {
        if(fboard[2])
        {
            moveNum++;
            selection=2;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
    else if(mouseX>=536 && mouseX<=700 && mouseY>=335 && mouseY<=500)
    {
        if(fboard[3])
        {
            moveNum++;
            selection=3;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
    else if(mouseX>=200 && mouseX<=365 && mouseY>=168 && mouseY<=334)
    {
        if(fboard[4])
        {
            moveNum++;
            selection=4;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
    else if(mouseX>=336 && mouseX<=535 && mouseY>=168 && mouseY<=334)
    {
        if(fboard[5])
        {
            moveNum++;
            selection=5;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
    else if(mouseX>=536 && mouseX<=700 && mouseY>=168 && mouseY<=334)
    {
        if(fboard[6])
        {
            moveNum++;
            selection=6;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
    else if(mouseX>=200 && mouseX<=365 && mouseY>=0 && mouseY<=167)
    {
        if(fboard[7])
        {
            moveNum++;
            selection=7;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }

    else if(mouseX>=336 && mouseX<=535 && mouseY>=0 && mouseY<=168)
    {
        if(fboard[8])
        {
            moveNum++;
            selection=8;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
    else if(mouseX>=536 && mouseX<=700 && mouseY>=0 && mouseY<=167)
    {
        if(fboard[9])
        {
            moveNum++;
            selection=9;
            fboard[selection]=0;
            board[selection]=(player==1)?'X':'O';
            showXO[selection]=player;
            player=(player==1?2:1);
        }
        if(sflag)
            PlaySound("sound\\Blip_Select.wav", NULL, SND_ASYNC);
    }
}




int gameOver(char box[]) // Returns 0 Draw, 10 X win, -10 O win, -1 Game not Over
{
    if(box[1]==box[2] && box[2]==box[3] && box[2]!=' ')
    {
        return box[2]=='X'? 10 : -10 ;
    }

    if(box[4]==box[5] && box[5]==box[6] && box[4]!=' ')
    {
        return box[4]=='X'? 10 : -10 ;
    }

    if(box[7]==box[8] && box[8]==box[9] && box[8]!=' ')
    {
        return box[8]=='X'? 10 : -10 ;
    }

    if(box[1]==box[4] && box[4]==box[7] && box[1]!=' ')
    {
        return box[1]=='X'? 10 : -10 ;
    }

    if(box[2]==box[5] && box[5]==box[8] && box[2]!=' ')
    {
        return box[2]=='X'? 10 : -10 ;
    }

    if(box[3]==box[6] && box[6]==box[9] && box[3]!=' ')
    {
        return box[3]=='X'? 10 : -10 ;
    }

    if(box[1]==box[5] && box[5]==box[9] && box[1]!=' ')
    {
        return box[1]=='X'? 10 : -10 ;
    }

    if(box[3]==box[5] && box[5]==box[7] && box[3]!=' ')
    {
        return box[3]=='X'? 10 : -10 ;
    }

    if(moveNum>=9)
    {
        return 0;
    }

    return -1;
}

void lineChecker(char box[])
{
    if(box[1]==box[2] && box[2]==box[3] && box[2]!=' ')
    {
        line=1;
    }
    if(box[4]==box[5] && box[5]==box[6] && box[4]!=' ')
    {
        line=2;
    }
    if(box[7]==box[8] && box[8]==box[9] && box[8]!=' ')
    {
        line=3;
    }
    if(box[1]==box[4] && box[4]==box[7] && box[1]!=' ')
    {
        line=4;
    }
    if(box[2]==box[5] && box[5]==box[8] && box[2]!=' ')
    {
        line=5;
    }
    if(box[3]==box[6] && box[6]==box[9] && box[3]!=' ')
    {
        line=6;
    }
    if(box[1]==box[5] && box[5]==box[9] && box[1]!=' ')
    {
        line=7;
    }
    if(box[3]==box[5] && box[5]==box[7] && box[3]!=' ')
    {
        line=8;
    }
    if(moveNum>=9)
    {
        line=0;
    }
}




int singlePlayer()
{
    int i,x;
    for(i=1; i<10; i++)
    {
        if(board[i]==' ')
        {
            board[i]='X';
            x=gameOver(board);
            if(x==10)
            {
                selection=i;
                board[selection]='X';
                showXO[selection]=1;
                fboard[selection]=0;
                moveNum++;
                return 0;
            }
            board[i]=' ';
        }
    }
    miniMax(board,1);
    selection=xoxo[0];
    moveNum++;
    fboard[selection]=0;
    board[selection]='X';
    showXO[selection]=1;
    player=(player==1?2:1);
}

int miniMax(char box[], int Player)
{
    int x=gameOver(box);
    int i,j,k,l,index;

    if(x!=-1)
    {
        xoxo[0]=-1;
        xoxo[1]=x;
        return 0;
    }

    if(Player==1)
    {
        int mx=-100;
        for(i=1; i<10; i++)
        {
            if(box[i]==' ')
            {
                box[i]='X';
                moveNum++;
                miniMax(box, !Player);
                if(mx < xoxo[1])
                {
                    mx=xoxo[1];
                    index=i;
                }
                box[i]=' ';
                moveNum--;
            }
        }
        xoxo[0]=index;
        xoxo[1]=mx;
        return 0;
    }
    else
    {

        int mn=+100;

        for(i=1; i<10; i++)
        {
            if(box[i]==' ')
            {
                box[i]='O';
                moveNum++;
                miniMax(box, !Player);
                if(mn > xoxo[1])
                {
                    mn=xoxo[1];
                    index=i;
                }

                box[i]=' ';
                moveNum--;
            }
        }
        xoxo[0]=index;
        xoxo[1]=mn;
        return 0;
    }
}

void init()
{
    int i;
    for(i=0; i<10; i++)
        fboard[i]=1;
    for(i=0; i<10; i++)
        board[i]=' ';
    for(i=0; i<10; i++)
        showXO[i]=0;
    moveNum=0;
    line=-1;
    m=0;
    c1=0;
    sw=0,sl=0,sd=0;
    scount=0;

    if(player==1)
        player=1;
    else
        player=2;
}

void showNum(int n,int x1,int y1,int x2,int y2,int font)
{
    int n1,n2;
    n1=n/10;
    n2=n%10;
    if(font==1)
    {
        if(n1==0)
            iShowBMP(x1,y1,"numbers\\num0.bmp");
        else if(n1==1)
            iShowBMP(x1,y1,"numbers\\num1.bmp");
        else if(n1==2)
            iShowBMP(x1,y1,"numbers\\num2.bmp");
        else if(n1==3)
            iShowBMP(x1,y1,"numbers\\num3.bmp");
        else if(n1==4)
            iShowBMP(x1,y1,"numbers\\num4.bmp");
        else if(n1==5)
            iShowBMP(x1,y1,"numbers\\num5.bmp");
        else if(n1==6)
            iShowBMP(x1,y1,"numbers\\num6.bmp");
        else if(n1==7)
            iShowBMP(x1,y1,"numbers\\num7.bmp");
        else if(n1==8)
            iShowBMP(x1,y1,"numbers\\num8.bmp");
        else if(n1==9)
            iShowBMP(x1,y1,"numbers\\num9.bmp");
        if(n2==0)
            iShowBMP(x2,y2,"numbers\\num0.bmp");
        else if(n2==1)
            iShowBMP(x2,y2,"numbers\\num1.bmp");
        else if(n2==2)
            iShowBMP(x2,y2,"numbers\\num2.bmp");
        else if(n2==3)
            iShowBMP(x2,y2,"numbers\\num3.bmp");
        else if(n2==4)
            iShowBMP(x2,y2,"numbers\\num4.bmp");
        else if(n2==5)
            iShowBMP(x2,y2,"numbers\\num5.bmp");
        else if(n2==6)
            iShowBMP(x2,y2,"numbers\\num6.bmp");
        else if(n2==7)
            iShowBMP(x2,y2,"numbers\\num7.bmp");
        else if(n2==8)
            iShowBMP(x2,y2,"numbers\\num8.bmp");
        else if(n2==9)
            iShowBMP(x2,y2,"numbers\\num9.bmp");
    }
    else if(font==2)
    {
        if(n1==0)
            iShowBMP(x1,y1,"numbers\\num0v.bmp");
        else if(n1==1)
            iShowBMP(x1,y1,"numbers\\num1v.bmp");
        else if(n1==2)
            iShowBMP(x1,y1,"numbers\\num2v.bmp");
        else if(n1==3)
            iShowBMP(x1,y1,"numbers\\num3v.bmp");
        else if(n1==4)
            iShowBMP(x1,y1,"numbers\\num4v.bmp");
        else if(n1==5)
            iShowBMP(x1,y1,"numbers\\num5v.bmp");
        else if(n1==6)
            iShowBMP(x1,y1,"numbers\\num6v.bmp");
        else if(n1==7)
            iShowBMP(x1,y1,"numbers\\num7v.bmp");
        else if(n1==8)
            iShowBMP(x1,y1,"numbers\\num8v.bmp");
        else if(n1==9)
            iShowBMP(x1,y1,"numbers\\num9v.bmp");
        if(n2==0)
            iShowBMP(x2,y2,"numbers\\num0v.bmp");
        else if(n2==1)
            iShowBMP(x2,y2,"numbers\\num1v.bmp");
        else if(n2==2)
            iShowBMP(x2,y2,"numbers\\num2v.bmp");
        else if(n2==3)
            iShowBMP(x2,y2,"numbers\\num3v.bmp");
        else if(n2==4)
            iShowBMP(x2,y2,"numbers\\num4v.bmp");
        else if(n2==5)
            iShowBMP(x2,y2,"numbers\\num5v.bmp");
        else if(n2==6)
            iShowBMP(x2,y2,"numbers\\num6v.bmp");
        else if(n2==7)
            iShowBMP(x2,y2,"numbers\\num7v.bmp");
        else if(n2==8)
            iShowBMP(x2,y2,"numbers\\num8v.bmp");
        else if(n2==9)
            iShowBMP(x2,y2,"numbers\\num9v.bmp");
    }
    else if(font==3)
    {
        if(n1==0)
            iShowBMP(x1,y1,"numbers\\num0d.bmp");
        else if(n1==1)
            iShowBMP(x1,y1,"numbers\\num1d.bmp");
        else if(n1==2)
            iShowBMP(x1,y1,"numbers\\num2d.bmp");
        else if(n1==3)
            iShowBMP(x1,y1,"numbers\\num3d.bmp");
        else if(n1==4)
            iShowBMP(x1,y1,"numbers\\num4d.bmp");
        else if(n1==5)
            iShowBMP(x1,y1,"numbers\\num5d.bmp");
        else if(n1==6)
            iShowBMP(x1,y1,"numbers\\num6d.bmp");
        else if(n1==7)
            iShowBMP(x1,y1,"numbers\\num7d.bmp");
        else if(n1==8)
            iShowBMP(x1,y1,"numbers\\num8d.bmp");
        else if(n1==9)
            iShowBMP(x1,y1,"numbers\\num9d.bmp");
        if(n2==0)
            iShowBMP(x2,y2,"numbers\\num0d.bmp");
        else if(n2==1)
            iShowBMP(x2,y2,"numbers\\num1d.bmp");
        else if(n2==2)
            iShowBMP(x2,y2,"numbers\\num2d.bmp");
        else if(n2==3)
            iShowBMP(x2,y2,"numbers\\num3d.bmp");
        else if(n2==4)
            iShowBMP(x2,y2,"numbers\\num4d.bmp");
        else if(n2==5)
            iShowBMP(x2,y2,"numbers\\num5d.bmp");
        else if(n2==6)
            iShowBMP(x2,y2,"numbers\\num6d.bmp");
        else if(n2==7)
            iShowBMP(x2,y2,"numbers\\num7d.bmp");
        else if(n2==8)
            iShowBMP(x2,y2,"numbers\\num8d.bmp");
        else if(n2==9)
            iShowBMP(x2,y2,"numbers\\num9d.bmp");
    }
    else if(font==4)
    {
        if(n1==0)
            iShowBMP(x1,y1,"timer\\num0.bmp");
        else if(n1==1)
            iShowBMP(x1,y1,"timer\\num1.bmp");
        else if(n1==2)
            iShowBMP(x1,y1,"timer\\num2.bmp");
        else if(n1==3)
            iShowBMP(x1,y1,"timer\\num3.bmp");
        else if(n1==4)
            iShowBMP(x1,y1,"timer\\num4.bmp");
        else if(n1==5)
            iShowBMP(x1,y1,"timer\\num5.bmp");
        else if(n1==6)
            iShowBMP(x1,y1,"timer\\num6.bmp");
        else if(n1==7)
            iShowBMP(x1,y1,"timer\\num7.bmp");
        else if(n1==8)
            iShowBMP(x1,y1,"timer\\num8.bmp");
        else if(n1==9)
            iShowBMP(x1,y1,"timer\\num9.bmp");
        if(n2==0)
            iShowBMP(x2,y2,"timer\\num0.bmp");
        else if(n2==1)
            iShowBMP(x2,y2,"timer\\num1.bmp");
        else if(n2==2)
            iShowBMP(x2,y2,"timer\\num2.bmp");
        else if(n2==3)
            iShowBMP(x2,y2,"timer\\num3.bmp");
        else if(n2==4)
            iShowBMP(x2,y2,"timer\\num4.bmp");
        else if(n2==5)
            iShowBMP(x2,y2,"timer\\num5.bmp");
        else if(n2==6)
            iShowBMP(x2,y2,"timer\\num6.bmp");
        else if(n2==7)
            iShowBMP(x2,y2,"timer\\num7.bmp");
        else if(n2==8)
            iShowBMP(x2,y2,"timer\\num8.bmp");
        else if(n2==9)
            iShowBMP(x2,y2,"timer\\num9.bmp");
    }
    else if(font==5)
    {
        if(n1==0)
            iShowBMP(x1,y1,"timer\\num0r.bmp");
        else if(n1==1)
            iShowBMP(x1,y1,"timer\\num1r.bmp");
        else if(n1==2)
            iShowBMP(x1,y1,"timer\\num2r.bmp");
        else if(n1==3)
            iShowBMP(x1,y1,"timer\\num3r.bmp");
        if(n2==0)
            iShowBMP(x2,y2,"timer\\num0r.bmp");
        else if(n2==1)
            iShowBMP(x2,y2,"timer\\num1r.bmp");
        else if(n2==2)
            iShowBMP(x2,y2,"timer\\num2r.bmp");
        else if(n2==3)
            iShowBMP(x2,y2,"timer\\num3r.bmp");
    }

}


void timer(void)
{
    int c=moveNum;
    if(c1!=c)
    {
        if(level==1)
        {
            countDown=31;
        }
        else if(level==2)
        {
            countDown=21;
        }
        else if(level==3)
        {
            countDown=11;
        }
        c1=c;
    }

    if(fscreen==1 || fscreen==2)
    {
        countDown--;
        if(countDown==3)
        {
            if(sflag)
            {
                PlaySound("sound\\Tick_Tock.wav",NULL,SND_ASYNC);
            }
        }


        if(countDown==-1)
        {
            fscreen=3;
        }
    }
}

void gif(void)
{
    gif1++;
    gif1=gif1%7;
}

void gifbb(void)
{
    gifb++;
    gifb=gifb%3;
}

void drawLine(int line)
{

    if(line==1)
    {
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
            iShowBMP(230,415,"images\\crosslineh.bmp");
    }
    else if(line==2)
    {
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
            iShowBMP(230,248,"images\\crosslineh.bmp");
    }
    else if(line==3)
    {
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
            iShowBMP(230,80,"images\\crosslineh.bmp");
    }
    else if(line==4)
    {
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
            iShowBMP(287,15,"images\\crosslinev.bmp");
    }
    else if(line==5)
    {
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
            iShowBMP(455,15,"images\\crosslinev.bmp");
    }
    else if(line==6)
    {
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
            iShowBMP(625,15,"images\\crosslinev.bmp");
    }
    else if(line==7)
    {
        iRotate(673,45,135);
        iSetColor(243,98,135);
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
        {
            iFilledRectangle(673,45,600,8);
        }
        iUnRotate();
    }
    else if(line==8)
    {
        iRotate(253,45,45);
        iSetColor(243,98,135);
        if(gif1==0 || gif1==2 || gif1==4 || gif1==6)
        {
            iFilledRectangle(253,45,600,8);
        }
        iUnRotate();
    }
}

void Pause(void)
{
    if(line>=0)
    {
        m++;
    }
    if(m==5)
    {
        if(gameOver(board)==10)
        {
            winner=1;
            counterp1++;
        }
        else if(gameOver(board)==-10)
        {
            winner=2;
            counterp2++;
        }
        else if(gameOver(board)==0)
        {
            winner=3;
            counterdraw++;
        }
    }

    if(m==30 && gameOver(board)!=-1)
    {
        fscreen=3;
        line=-1;
        m=0;
    }
}

int main()
{
    t=iSetTimer(1000,timer);
    t1=iSetTimer(100,Pause);
    t2=iSetTimer(300,gif);
    t3=iSetTimer(500,gifbb);
    iInitialize(900,500,"Tic Tac Toe");

    return 0;
}


'THE FAST FOURIER TRANSFORM
'Upon entry, N% contains the number of points in the DFT, REX[ ] and
'IMX[ ] contain the real and imaginary parts of the input. Upon return,
'REX[ ] and IMX[ ] contain the DFT output. All signals run from 0 to N%-1.
'
PI = 3.14159265 'Set constants
NM1% = N%-1
ND2% = N%/2
M% = CINT(LOG(N%)/LOG(2))
J% = ND2%
'
FOR I% = 1 TO N%-2 'Bit reversal sorting
IF I% >= J% THEN GOTO 1190
TR = REX[J%]
TI = IMX[J%]
REX[J%] = REX[I%]
IMX[J%] = IMX[I%]
REX[I%] = TR
IMX[I%] = TI
K% = ND2%
IF K% > J% THEN GOTO 1240
J% = J%-K%
K% = K%/2
GOTO 1200
J% = J%+K%
NEXT I%
'
FOR L% = 1 TO M% 'Loop for each stage
LE% = CINT(2^L%)
LE2% = LE%/2
UR = 1
UI = 0
SR = COS(PI/LE2%) 'Calculate sine & cosine values
SI = -SIN(PI/LE2%)
FOR J% = 1 TO LE2% 'Loop for each sub DFT
JM1% = J%-1
FOR I% = JM1% TO NM1% STEP LE% 'Loop for each butterfly
IP% = I%+LE2%
TR = REX[IP%]*UR - IMX[IP%]*UI 'Butterfly calculation
TI = REX[IP%]*UI + IMX[IP%]*UR
REX[IP%] = REX[I%]-TR
IMX[IP%] = IMX[I%]-TI
REX[I%] = REX[I%]+TR
IMX[I%] = IMX[I%]+TI
NEXT I%
TR = UR
UR = TR*SR - UI*SI
UI = TR*SI + UI*SR
NEXT J%
NEXT L%
'
RETURN
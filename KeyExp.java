class KeyExp{
    public static int[][] keyExpansion = new int[4][60];

    public void keyExpansion(int[][] key, int round) {

        /*int[][] key1 = {
            {0x60,  0x15,  0x2b,  0x85,  0x1f,  0x3b,  0x2d,  0x09},
            {0x3d,  0xca,  0x73,  0x7d,  0x35,  0x61,  0x98,  0x14},
            {0xeb,  0x71,  0xae,  0x77,  0x2c,  0x08,  0x10,  0xdf},
            {0x10,  0xbe,  0xf0,  0x81,  0x07,  0xd7,  0xa3,  0xf4},
        };*/

        SBoxes sBox = new SBoxes();

        int i;
        int j;
        int ans;
        int rconRow;
        int start = 8;
        int column = 4 + (round*4);
        int[][] arrayB = new int[4][1];

        // copy key to key expansion
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 4; j++) {
                keyExpansion[j][i] = key[j][i];
            }
        }

        // Key expansion algoritham
        for(i=8;i<column;i++){
            if(i%8 == 0){
                copyArray(arrayB, i-1);
                rotWorld(arrayB);
                subBytes(arrayB);
                rconRow = i/8;
                for(j=0;j<4;j++){
                    ans = arrayB[j][0] ^ keyExpansion[j][i-8] ^ sBox.RCON[j][rconRow - 1];
                    keyExpansion[j][i] = ans;
                }
            }else if(i%8 == 4){
                copyArray(arrayB, i-1);
                subBytes(arrayB);
                for(j=0;j<4;j++){
                    ans = arrayB[j][0] ^ keyExpansion[j][i-8];
                    keyExpansion[j][i] = ans;
                }
            }
            else{
                for(j=0;j<4;j++){
                    ans = keyExpansion[j][i-1] ^ keyExpansion[j][i-8];
                    keyExpansion[j][i] = ans;
                }
            }
        }
        showKey();
    }

    private void copyArray(int[][] arrayB, int row) {
        int i;
        for (i = 0; i < 4; i++) {
            arrayB[i][0] = keyExpansion[i][row];
        }
    }

    private void rotWorld(int[][] arrayB) {
        int i;
        int temp;
        temp = arrayB[0][0];
        for (i = 0; i < 3; i++) {
            arrayB[i][0] = arrayB[i + 1][0];
        }
        arrayB[i][0] = temp;
    }

    private void subBytes(int[][] arrayB) {
        AESUTIL util = new AESUTIL();
        SBoxes sBox = new SBoxes();

        int i = 0;
        String temp;
        String co_x, co_y;
        for (i = 0; i < 4; i++) {
            temp = util.hexToString(arrayB[i][0]);
            char[] coordinates = temp.toCharArray();
            if (coordinates.length == 1) {
                co_x = "0";
                co_y = Character.toString(coordinates[0]);
            } else {
                co_x = Character.toString(coordinates[0]);
                co_y = Character.toString(coordinates[1]);
            }
            int x = util.convertToHex(co_x);
            int y = util.convertToHex(co_y);
            arrayB[i][0] = sBox.SBOX[x][y];
        }
    }

    public int[][] addRoundKey(int[][] state, int round) {
        int i, j;
        int ans;
        round = round * 4;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                ans = state[j][i] ^ keyExpansion[j][i + round];
                state[j][i] = ans;
            }
        }
        return state;
    }

    public void showKey(){
        AESUTIL util = new AESUTIL();
        for(int i = 0;i<4;i++){
            for(int j=0;j<60;j++){
                System.out.print(util.hexToString(keyExpansion[i][j])+" ");
            }
            System.out.println();
        }
    }
}
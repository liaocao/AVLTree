import java.util.ArrayList;

public class AVLTree<K extends Comparable<K>, V> implements Map<K, V>{

    private class Node{
        public K key;
        public V value;
        public Node left, right;
        public int height;

        public Node(K key, V value){
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            height = 1;//从根节点出发去找他的添加位置，这个位置一定是叶子节点
        }
    }

    private Node root;
    private int size;

    public AVLTree(){
        root = null;
        size = 0;
    }

    //判断该二叉树是否是一颗二分搜索树
    public boolean isBST(){
        //对于二分搜索树，当使用中序进行遍历，遍历的结果是所有的元素按顺序排列的

        ArrayList<K> keys = new ArrayList<>();
        inOrder(root, keys);
        for(int i = 1; i < keys.size(); i++){
            if(keys.get(i - 1).compareTo(keys.get(i)) > 0){//如果前一个比后一个大，说明就没有维持住二分搜索树的性质
                return false;
            }
        }
        return true;
    }

    private void inOrder(Node node, ArrayList<K> keys){
        if(node == null)
            return;

        inOrder(node.left, keys);
        keys.add(node.key);
        inOrder(node.right, keys);
    }

    //判断该二叉树是否是一棵平衡二叉树
    public boolean isBalanced(){
        return isBalanced(root);
    }

    //判断以Node为根的二叉树是否是一棵平衡二叉树，递归算法
    private boolean isBalanced(Node node){
        if(node == null)
            return true;

        int balanceFactor = getBalanceFactor(node);
        if(Math.abs(balanceFactor) > 1)
            return false;

        return isBalanced(node.left) && isBalanced(node.right);
    }

    //获得节点node的平衡因子
    private int getBalanceFactor(Node node){
        if(node == null)
            return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    //获得节点node的高度值
    private int getHeight(Node node){
        if(node == null)
            return 0;
        return node.height;
    }

    //向二分搜索树中添加新的元素(key, value)
    @Override
    public void add(K key, V value) {
        root = add(root, key, value);
    }

    //向以node为根的二分搜索树中插入元素（key, value），递归算法
    //返回插入新节点后二分搜索树的根
    private Node add(Node node, K key, V value){
        if(node == null){
            size ++;
            return new Node(key, value);
        }

        if(key.compareTo(node.key) < 0){
            node.left = add(node.left, key, value);
        }else if(key.compareTo(node.key) > 0){
            node.right = add(node.right, key, value);
        }else{
            node.value = value;//这里的设计是把原先的值覆盖了
        }
        //对于以当前node为根的二分搜索树，添加了一个新的节点之后，相应的需要对当前的node的height值进行更新
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        //之所以不是对自身加1，是因为，添加节点后，未必会提高高度，以左右子树中高的为准反而更合适

        //计算平衡因子
        int balanceFactor = getBalanceFactor(node);
        if(Math.abs(balanceFactor) > 1){//大于1时，二叉树不再满足平衡二叉树的条件
            System.out.println("unbalanced：" + balanceFactor);
        }
        return node;
    }

    //返回以node为根节点的二分搜索树中，key所在的节点
    private Node getNode(Node node, K key){
        if(node == null){
            return null;
        }

        if(key.compareTo(node.key) == 0){
            return node;
        }else if(key.compareTo(node.key) < 0){
            return getNode(node.left, key);
        }else{
            return getNode(node.right, key);
        }
    }

    //从二分搜索树中删除键为key的节点
    @Override
    public V remove(K key) {
        Node node = getNode(root, key);
        if(node != null){
            root = remove(root, key);
            return node.value;
        }

        return null;
    }

    //返回以node为根的二分搜索树的最小值所在的节点
    private Node minimum(Node node){
        if(node.left == null){
            return node;
        }

        return minimum(node.left);
    }

    //删除掉以node为根的二分搜索树的最小节点
    //返回删除节点后新的二分搜索树的根
    private Node removeMin(Node node){
        if(node.left == null){//当left为null，就说明node已经是最小节点了
            Node rightNode = node.right;
            node.right = null;
            size --;
            return rightNode;
        }

        node.left = removeMin(node.left);
        return node;
    }

    //删除掉以node为根的二分搜索树中的最大节点
    //返回删除节点后新的二分搜索树的根
    private Node removeMax(Node node){

        if(node.right == null){//这是往右递归到底的情况
            Node leftNode = node.left;
            node.left = null;
            size --;
            return leftNode;
        }

        node.right = removeMax(node.right);//要删除node的右子树对应的最大值，同时返回新右子树的根
        return node;
    }

    //删除掉以node为根的二分搜索树中键为key的节点，递归算法
    //返回删除节点后新的二分搜索树的根
    private Node remove(Node node, K key){
        if(node == null)
            return null;

        if(key.compareTo(node.key) < 0){
            node.left = remove(node.left, key);
            return node;
        }else if(key.compareTo(node.key) > 0){
            node.right = remove(node.right, key);
            return node;
        }else {//这是等于的情况
            if(node.left == null){//左子树为空，把右子树返回
                Node rightNode = node.right;
                node.right = null;
                size --;
                return rightNode;
            }else if(node.right == null){
                Node leftNode = node.left;
                node.left = null;
                size --;
                return leftNode;
            }else{//左右子树均不为空的情况
                //找到比待删除节点大的最小节点，即待删除节点右子树的最小节点
                //用这个节点顶替待删除节点的位置
                Node successor = minimum(node.right);
                successor.right = removeMin(node.right);//在方法里面就做了size--，所以不需要在这里做了
                successor.left = node.left;

                node.left = node.right = null;
                return successor;
            }
        }
    }

    @Override
    public boolean contains(K key) {

        return getNode(root, key) != null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(root, key);
        return node == null ? null : node.value;
    }

    @Override
    public void set(K key, V newValue) {
        Node node = getNode(root, key);
        if(node == null)
            throw new IllegalArgumentException(key + " doesn't exist");

        node.value = newValue;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public static void main(String[] args) {
        System.out.println("A-Tale-of-Two-Cities.txt");

        ArrayList<String> words = new ArrayList<>();
        if(FileOperation.readFile("A-Tale-of-Two-Cities.txt", words)){
            System.out.println("Total words: " + words.size());

            AVLTree<String, Integer> map = new AVLTree<>();
            for(String word: words){
                if(map.contains(word)) {
                    map.set(word, map.get(word) + 1);
                }else{
                    map.add(word, 1);
                }//保存了每一个单词对应出现的次数是多少
            }

            System.out.println("Total different words: " + map.getSize());
            System.out.println("Frequency of TALE: " + map.get("tale"));
            System.out.println("Frequency of you: " + map.get("you"));

            System.out.println("is BST : " + map.isBST());
            System.out.println("is Balanced : " + map.isBalanced());
        }
    }
}
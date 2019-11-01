package Parser;

import Window.mainWindow;

/**
 * @author knight
 * @date 2019/11/1 8:04
 * created
 */
public class DynamicException {
    public interface dynamicException{
        void errorInfo();
    }


    private void addErrorInfo(String msg){
        Parser.errors.add(msg);
        throw new stopMachineException();
    }


    public class noArrayException extends Exception implements dynamicException{
        @Override
        public void errorInfo(){
            addErrorInfo("在第"+ mainWindow.j+"条指令中，数组未初始化");
        }
    }

    public class outOfArrayBoundException extends Exception implements dynamicException{
        @Override
        public void errorInfo() {
            addErrorInfo("在第"+ mainWindow.j+"条指令中，对数组的取值超出上下界");
        }
    }

    public class undeclaredIdentifierException extends Exception implements dynamicException{
        @Override
        public void errorInfo() {
            addErrorInfo("在第"+ mainWindow.j+"条指令中，使用了未声明的标识符");
        }
    }

    public class unInitializedIdentifierException extends Exception implements dynamicException{
        @Override
        public void errorInfo() {
            addErrorInfo("在第"+ mainWindow.j+"条指令中，使用了未初始化的标识符");
        }
    }


    public class classCastException extends Exception implements dynamicException{
        @Override
        public void errorInfo() {
            addErrorInfo("在第"+ mainWindow.j+"条指令中，类型不匹配，数据类型转换错误");
        }
    }

    public class mismatchOperatorException extends Exception implements dynamicException{
        @Override
        public void errorInfo() {
            addErrorInfo("在第"+ mainWindow.j+"条指令中，运算符与操作数不匹配");
        }
    }

    public class stopMachineException extends RuntimeException implements dynamicException{
        @Override
        public void errorInfo() {
            addErrorInfo("解释器停止工作");
        }
    }

    public class defaultException extends Exception implements dynamicException{
        private String mes;

        public defaultException(String mes){
            this.mes = mes;
        }

        @Override
        public void errorInfo() {
            addErrorInfo("在第"+ mainWindow.j+"条指令中，"+mes);
        }
    }


}

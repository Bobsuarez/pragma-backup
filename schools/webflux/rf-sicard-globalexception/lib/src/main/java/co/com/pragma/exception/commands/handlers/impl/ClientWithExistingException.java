package co.com.pragma.exception.commands.handlers.impl;

//public class ClientWithExistingException implements ExceptionHandler {
//
//    @Override
//    public ResponseDTO<Object> handleException(Throwable exception) {
//
//        ClientWithExistingRequestException ex = (ClientWithExistingRequestException) exception;
//
//        return ResponseDTO.builder()
//                .code(HttpStatus.CONFLICT.value())
//                .message(HttpStatus.CONFLICT.getReasonPhrase())
//                .error(getErrorDTOMap(ex.getClass().getSimpleName(), ex.getMessage()))
//                .build();
//    }
//
//    @Override
//    public Class<? extends Throwable> getExceptionClass() {
//        return null;
//    }
//}
